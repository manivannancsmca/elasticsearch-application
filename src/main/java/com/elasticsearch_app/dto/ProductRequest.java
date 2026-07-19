package com.elasticsearch_app.dto;


import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {

    @NotBlank
    @Size(min = 2, max = 200)
    private String name;

    @NotBlank
    @Size(min = 10, max = 5000)
    private String description;

    @NotBlank
    @Pattern(regexp = "^[A-Z0-9-]+$")
    private String sku;

    @NotBlank
    private String category;

    private String subCategory;

    @NotBlank
    private String brand;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal price;

    private BigDecimal originalPrice;

    @NotNull
    @Min(0)
    private Integer stockQuantity;

    @NotNull
    private Boolean isActive;

    private Float rating;
    private Integer reviewCount;
    private List<String> tags;
    private List<String> colors;
    private List<String> sizes;
    private Map<String, Object> specifications;

    @Valid
    private List<VariantRequest> variants;

    private String warehouseLocation;
    private List<String> shippingCountries;
    private Integer salesCount;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VariantRequest {
        @NotBlank
        private String variantId;
        private String color;
        private String size;
        @NotNull
        private BigDecimal price;
        @NotNull
        @Min(0)
        private Integer stock;
        @NotBlank
        private String sku;
    }
}
