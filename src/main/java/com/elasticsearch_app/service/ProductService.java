package com.elasticsearch_app.service;

import java.util.List;

import com.elasticsearch_app.dto.ProductRequest;
import com.elasticsearch_app.dto.ProductResponse;
import com.elasticsearch_app.dto.ProductSearchRequest;
import com.elasticsearch_app.dto.ProductSearchResponse;

public interface ProductService {

    /**
     * Product Service Interface
     * Defines all operations for product search, indexing, and management
     */
    ProductSearchResponse searchProducts(ProductSearchRequest request);

    /**
     * Get product by ID
     */
    ProductResponse getProductById(String id);

    /**
     * Index a new product
     */
    ProductResponse indexProduct(ProductRequest request);

    /**
     * Bulk index products
     */
    List<ProductResponse> bulkIndexProducts(List<ProductRequest> requests);

    /**
     * Update product
     */
    ProductResponse updateProduct(String id, ProductRequest request);

    /**
     * Delete product
     */
    void deleteProduct(String id);

    /**
     * Get autocomplete suggestions
     */
    List<String> getSuggestions(String prefix);

    /**
     * Get similar products
     */
    List<ProductResponse> getSimilarProducts(String productId, int size);

    /**
     * Reindex all products
     */
    void reindexAllProducts();
}
