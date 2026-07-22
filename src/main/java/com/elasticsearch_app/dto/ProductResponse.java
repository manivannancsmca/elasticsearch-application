package com.elasticsearch_app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {

    private String id;
    private String name;
    private String description;
    private String sku;
    private String category;
    private String subCategory;
    private String brand;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Integer discountPercentage;
    private Integer stockQuantity;
    private Boolean inStock;
    private Float rating;
    private Integer reviewCount;
    private List<String> tags;
    private List<String> colors;
    private List<String> sizes;
    private Map<String, Object> specifications;
    private List<VariantResponse> variants;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Float score;
    private Map<String, List<String>> highlights;
    private Boolean shipsToUser;

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class VariantResponse {
        private String variantId;
        private String color;
        private String size;
        private BigDecimal price;
        private Integer stock;
        private String sku;
    }
}
