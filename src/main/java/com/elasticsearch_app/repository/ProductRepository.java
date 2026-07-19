package com.elasticsearch_app.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import com.elasticsearch_app.model.Product;

@Repository
public interface ProductRepository extends ElasticsearchRepository<Product, String>  {

    List<Product> findByName(String name);

    Page<Product> findByCategory(String category, Pageable pageable);

    List<Product> findByBrand(String brand);

    List<Product> findByIsActiveTrueAndInStockTrue();

    List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    List<Product> findByCategoryAndPriceBetween(String category, BigDecimal minPrice, BigDecimal maxPrice);

    List<Product> findByTagsContaining(String tag);

    List<Product> findByCategoryOrderByPriceAsc(String category);

    List<Product> findTop10ByOrderByRatingDesc();

    List<Product> findByNameContaining(String nameFragment);

    @Query("""
            {
              "multi_match": {
                "query": "?0",
                "fields": [
                  "name^3",
                  "description",
                  "brand^2",
                  "category"
                ],
                "type": "best_fields"
              }
            }
            """)
    List<Product> searchByText(String query);

    @Query("""
            {
              "multi_match": {
                "query": "?0",
                "fields": [
                  "name^3",
                  "description",
                  "brand^2"
                ],
                "fuzziness": "AUTO"
              }
            }
            """)
    List<Product> fuzzySearch(String query);
}
