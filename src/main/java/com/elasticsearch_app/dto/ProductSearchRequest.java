package com.elasticsearch_app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductSearchRequest {

    // Full-text search
    private String query;

    // Filters
    private List<String> categories;
    private List<String> brands;
    private List<String> colors;
    private List<String> sizes;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Boolean inStock;
    private Float minRating;
    private List<String> tags;
    private String shippingCountry;
    private String geoLocation;
    private Double maxDistanceKm;

    // Sorting
    private String sortBy;
    private String sortDirection;

    // Pagination
    @Builder.Default
    private Integer page = 0;
    @Builder.Default
    private Integer size = 20;

    // Options
    @Builder.Default
    private Boolean includeAggregations = true;
    @Builder.Default
    private Boolean highlight = true;
    private Float minScore;
}
