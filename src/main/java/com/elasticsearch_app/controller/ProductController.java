package com.elasticsearch_app.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.elasticsearch_app.dto.ProductRequest;
import com.elasticsearch_app.dto.ProductResponse;
import com.elasticsearch_app.dto.ProductSearchRequest;
import com.elasticsearch_app.dto.ProductSearchResponse;
import com.elasticsearch_app.service.ProductService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.indexProduct(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getById(@PathVariable String id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> update(@PathVariable String id, @Valid @RequestBody ProductRequest request) {
        return ResponseEntity.ok(productService.updateProduct(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search/query")
    public ResponseEntity<List<String>> suggest(
            @RequestParam(name = "ask", required = false, defaultValue = "") String ask) {
        log.info("ask ::::: {} ", ask);
        if (ask.isBlank()) {
            return ResponseEntity.ok(Collections.emptyList());
        }
        return ResponseEntity.ok(productService.getSuggestions(ask));
    }

    @GetMapping("/{id}/similar")
    public ResponseEntity<List<ProductResponse>> getSimilar(@PathVariable String id,
            @RequestParam(defaultValue = "5") int size) {
        return ResponseEntity.ok(productService.getSimilarProducts(id, size));
    }

    @PostMapping("/reindex")
    public ResponseEntity<Void> reindexAll() {
        productService.reindexAllProducts();
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/search")
    public ResponseEntity<ProductSearchResponse> search(@RequestBody ProductSearchRequest request) {
        return ResponseEntity.ok(productService.searchProducts(request));
    }
}