package com.elasticsearch_app.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.elasticsearch.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import com.elasticsearch_app.dto.ProductRequest;
import com.elasticsearch_app.dto.ProductResponse;
import com.elasticsearch_app.dto.ProductSearchRequest;
import com.elasticsearch_app.dto.ProductSearchResponse;
import com.elasticsearch_app.exception.ProductNotFoundException;
import com.elasticsearch_app.mapper.ProductMapper;
import com.elasticsearch_app.model.Product;
import com.elasticsearch_app.repository.ProductRepository;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.StreamSupport;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final ElasticsearchClient esClient;

    private static final String INDEX_NAME = "products";

    @Override
    public ProductSearchResponse searchProducts(ProductSearchRequest request) {
        try {
            SearchResponse<Product> response = esClient.search(s -> s
                    .index(INDEX_NAME)
                    .query(buildCompoundQuery(request))
                    .from(request.getPage() * request.getSize())
                    .size(request.getSize())
                    .sort(sort -> {
                        if (request.getSortBy() != null) {
                            sort.field(f -> f.field(request.getSortBy())
                                    .order("desc".equalsIgnoreCase(request.getSortDirection()) ? SortOrder.Desc
                                            : SortOrder.Asc));
                        }
                        return sort;
                    })
                    .aggregations(request.getIncludeAggregations() ? buildFacets() : Collections.emptyMap())
                    .highlight(h -> h
                            .fields("name", f -> f)
                            .fields("description", f -> f)),
                    Product.class);

            return processSearchHits(response, request);
        } catch (IOException e) {
            log.error("Elasticsearch system error during structural search validation", e);
            throw new RuntimeException("Search pipeline failed execution context", e);
        }
    }

    @Override
    public ProductResponse getProductById(String id) {
        return productRepository.findById(id)
                .map(productMapper::toResponse)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
    }

    @Override
    public ProductResponse indexProduct(ProductRequest request) {
        log.info("Indexing new product with SKU: {}", request.getSku());

        Product productEntity = productMapper.toEntity(request);
        Product savedProduct = productRepository.save(productEntity);

        return productMapper.toResponse(savedProduct);
    }

    @Override
    public List<ProductResponse> bulkIndexProducts(List<ProductRequest> requests) {
        List<Product> entities = requests.stream().map(productMapper::toEntity).toList();
        Iterable<Product> savedEntities = productRepository.saveAll(entities);
        return StreamSupport.stream(savedEntities.spliterator(), false)
                .map(productMapper::toResponse)
                .toList();
    }

    @Override
    public ProductResponse updateProduct(String id, ProductRequest request) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));

        // Update fields safely using Java pattern updates
        Product updatedEntity = productMapper.toEntity(request);
        updatedEntity.setId(existingProduct.getId());
        updatedEntity.setCreatedAt(existingProduct.getCreatedAt());
        updatedEntity.setUpdatedAt(LocalDateTime.now());

        return productMapper.toResponse(productRepository.save(updatedEntity));
    }

    @Override
    public void deleteProduct(String id) {
        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }

    @Override
    public List<String> getSuggestions(String prefix) {
        String searchPrefix = prefix.trim().toLowerCase();
        log.info("prefix : {} ", searchPrefix);

        try {
            var response = esClient.search(s -> s
                    .index(INDEX_NAME)
                    .query(q -> q.matchPhrasePrefix(m -> m.field("searchSuggest").query(searchPrefix))),
                    Product.class);

            log.info("------SearchResponse: {}", response);

            // 1. Extract the hits list
            List<String> suggestions = response.hits().hits().stream()
                    .map(Hit::source)
                    .filter(Objects::nonNull)
                    .map(Product::getName)
                    .distinct()
                    .toList();

            // 2. If no products matched, throw your custom exception
            if (suggestions.isEmpty()) {
                throw new ProductNotFoundException("No products found matching the search criteria: " + prefix);
            }

            return suggestions;

        } catch (IOException e) {
            log.error("Elasticsearch connection failed", e);
            throw new RuntimeException("Internal search engine communication error", e);
        }
    }

    @Override
    public List<ProductResponse> getSimilarProducts(String productId, int size) {
        Product target = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Base context missing"));

        try {
            var response = esClient.search(s -> s
                    .index(INDEX_NAME)
                    .query(q -> q.bool(b -> b
                            .must(m -> m.match(t -> t.field("category").query(target.getCategory())))
                            .mustNot(mn -> mn.term(t -> t.field("id").value(target.getId())))))
                    .size(size), Product.class);

            return response.hits().hits().stream()
                    .map(Hit::source)
                    .map(productMapper::toResponse)
                    .toList();
        } catch (IOException e) {
            throw new ProductNotFoundException("No products found matching the search criteria: " + e);
        }
    }

    @Override
    public void reindexAllProducts() {
        Iterable<Product> all = productRepository.findAll();
        productRepository.saveAll(all);
    }

    private Query buildCompoundQuery(ProductSearchRequest request) {
        return Query.of(q -> q.bool(b -> {
            // Full-Text Search Block
            if (request.getQuery() != null && !request.getQuery().isBlank()) {
                b.must(m -> m.multiMatch(mm -> mm
                        .query(request.getQuery())
                        .fields("name^3", "description", "brand^2", "tags")
                        .fuzziness("AUTO")));
            }

            // High Precision Structural Filtering
            if (request.getCategories() != null && !request.getCategories().isEmpty()) {
                List<FieldValue> catValues = request.getCategories().stream().map(FieldValue::of).toList();
                b.filter(f -> f.terms(t -> t.field("category").terms(v -> v.value(catValues))));
            }
            if (request.getBrands() != null && !request.getBrands().isEmpty()) {
                List<FieldValue> brandValues = request.getBrands().stream().map(FieldValue::of).toList();
                b.filter(f -> f.terms(t -> t.field("brand").terms(v -> v.value(brandValues))));
            }
            if (request.getInStock() != null) {
                b.filter(f -> f.term(t -> t.field("inStock").value(request.getInStock())));
            }

            // Numeric Range Segment Allocation
            if (request.getMinPrice() != null || request.getMaxPrice() != null) {
                b.filter(f -> f.range(r -> r.number(n -> {
                    n.field("price");
                    if (request.getMinPrice() != null) {
                        n.gte(request.getMinPrice().doubleValue());
                    }
                    if (request.getMaxPrice() != null) {
                        n.lte(request.getMaxPrice().doubleValue());
                    }
                    return n;
                })));
            }

            // GeoLocation Spatial Query Parsing
            if (request.getGeoLocation() != null && request.getMaxDistanceKm() != null) {
                String[] parts = request.getGeoLocation().split(",");
                double lat = Double.parseDouble(parts[0].trim());
                double lon = Double.parseDouble(parts[1].trim());

                b.filter(f -> f.geoDistance(g -> g
                        .field("warehouseLocation")
                        .distance(request.getMaxDistanceKm() + "km")
                        .location(loc -> loc.latlon(ll -> ll.lat(lat).lon(lon)))));
            }

            return b;
        }));
    }

    private Map<String, Aggregation> buildFacets() {
        Map<String, Aggregation> aggregations = new HashMap<>();
        aggregations.put("categories", Aggregation.of(a -> a.terms(t -> t.field("category"))));
        aggregations.put("brands", Aggregation.of(a -> a.terms(t -> t.field("brand"))));
        aggregations.put("colors", Aggregation.of(a -> a.terms(t -> t.field("colors"))));
        aggregations.put("sizes", Aggregation.of(a -> a.terms(t -> t.field("sizes"))));
        aggregations.put("price_stats", Aggregation.of(a -> a.stats(s -> s.field("price"))));
        return aggregations;
    }

    private ProductSearchResponse processSearchHits(SearchResponse<Product> response, ProductSearchRequest request) {
        List<ProductResponse> products = response.hits().hits().stream().map(hit -> {
            Product p = hit.source();
            ProductResponse dto = productMapper.toResponse(p);
            if (dto != null) {
                dto.setScore(hit.score() != null ? hit.score().floatValue() : 0.0f);
                // Assign highlighted fragments if requested
                if (!hit.highlight().isEmpty()) {
                    Map<String, List<String>> highlights = new HashMap<>();
                    hit.highlight().forEach((k, v) -> highlights.put(k, v));
                    dto.setHighlights(highlights);
                }
            }
            return dto;
        }).filter(Objects::nonNull).toList();

        long totalHits = response.hits().total() != null ? response.hits().total().value() : 0L;
        int totalPages = (int) Math.ceil((double) totalHits / request.getSize());

        return ProductSearchResponse.builder()
                .products(products)
                .totalHits(totalHits)
                .totalPages(totalPages)
                .currentPage(request.getPage())
                .pageSize(request.getSize())
                .took(response.took())
                .build();
    }

}