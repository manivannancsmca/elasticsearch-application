package com.elasticsearch_app.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductSearchResponse {

    private List<ProductResponse> products;
    private Long totalHits;
    private Integer totalPages;
    private Integer currentPage;
    private Integer pageSize;
    private Long took;
    private AggregationResult aggregations;
    private List<String> suggestions;
    private String didYouMean;

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class AggregationResult {
        private List<BucketAggregation> categories;
        private List<BucketAggregation> brands;
        private List<PriceRangeAggregation> priceRanges;
        private List<BucketAggregation> colors;
        private List<BucketAggregation> sizes;
        private List<BucketAggregation> ratings;
        private PriceStats priceStats;
        private List<BucketAggregation> tags;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class BucketAggregation {
        private String key;
        private Long count;
        private Map<String, Object> subAggregations;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class PriceRangeAggregation {
        private String label;
        private Double from;
        private Double to;
        private Long count;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class PriceStats {
        private Double min;
        private Double max;
        private Double avg;
    }
}
