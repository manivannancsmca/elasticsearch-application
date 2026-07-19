package com.elasticsearch_app.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.GeoPointField;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "products")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Product {

    @Id
    private String id;

    @Field(type = FieldType.Text, analyzer = "standard", searchAnalyzer = "standard")
    private String name;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String description;

    @Field(type = FieldType.Keyword)
    private String sku;

    @Field(type = FieldType.Keyword)
    private String category;

    @Field(type = FieldType.Keyword)
    private String subCategory;

    @Field(type = FieldType.Keyword)
    private String brand;

    @Field(type = FieldType.Double)
    private BigDecimal price;

    @Field(type = FieldType.Double)
    private BigDecimal originalPrice;

    @Field(type = FieldType.Integer)
    private Integer stockQuantity;

    @Field(type = FieldType.Boolean)
    private Boolean inStock;

    @Field(type = FieldType.Boolean)
    private Boolean isActive;

    @Field(type = FieldType.Float)
    private Float rating;

    @Field(type = FieldType.Integer)
    private Integer reviewCount;

    @Field(type = FieldType.Keyword)
    private List<String> tags;

    @Field(type = FieldType.Keyword)
    private List<String> colors;

    @Field(type = FieldType.Keyword)
    private List<String> sizes;

    @Field(type = FieldType.Object)
    private Map<String, Object> specifications;

    @Field(type = FieldType.Nested)
    private List<ProductVariant> variants;

    @Field(type = FieldType.Date, format = {DateFormat.date_hour_minute_second_millis, DateFormat.strict_date_time})
    private LocalDateTime createdAt;

    @Field(type = FieldType.Date, format = {DateFormat.date_hour_minute_second_millis, DateFormat.strict_date_time})
    private LocalDateTime updatedAt;

    @GeoPointField
    private WarehouseLocation warehouseLocation;

    @Field(type = FieldType.Keyword)
    private List<String> shippingCountries;

    @Field(type = FieldType.Integer)
    private Integer salesCount;

    @Field(type = FieldType.Text, analyzer = "standard", searchAnalyzer = "standard")
    private String searchSuggest;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductVariant {
        @Field(type = FieldType.Keyword)
        private String variantId;
        @Field(type = FieldType.Keyword)
        private String color;
        @Field(type = FieldType.Keyword)
        private String size;
        @Field(type = FieldType.Double)
        private BigDecimal price;
        @Field(type = FieldType.Integer)
        private Integer stock;
        @Field(type = FieldType.Keyword)
        private String sku;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WarehouseLocation {
        private Double lat;
        private Double lon;
    }
}
