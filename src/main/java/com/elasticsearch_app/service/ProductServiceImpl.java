package com.elasticsearch_app.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.elasticsearch_app.dto.ProductRequest;
import com.elasticsearch_app.dto.ProductResponse;
import com.elasticsearch_app.dto.ProductSearchRequest;
import com.elasticsearch_app.dto.ProductSearchResponse;
import com.elasticsearch_app.exception.ProductNotFoundException;
import com.elasticsearch_app.mapper.ProductMapper;
import com.elasticsearch_app.model.Product;
import com.elasticsearch_app.repository.ProductRepository;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public ProductSearchResponse searchProducts(ProductSearchRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'searchProducts'");
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
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'bulkIndexProducts'");
    }

    @Override
    public ProductResponse updateProduct(String id, ProductRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateProduct'");
    }

    @Override
    public void deleteProduct(String id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteProduct'");
    }

    @Override
    public List<String> getSuggestions(String prefix) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getSuggestions'");
    }

    @Override
    public List<ProductResponse> getSimilarProducts(String productId, int size) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getSimilarProducts'");
    }

    @Override
    public void reindexAllProducts() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'reindexAllProducts'");
    }

}