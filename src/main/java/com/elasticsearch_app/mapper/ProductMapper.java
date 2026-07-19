package com.elasticsearch_app.mapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

import com.elasticsearch_app.dto.ProductRequest;
import com.elasticsearch_app.dto.ProductResponse;
import com.elasticsearch_app.model.Product;
import com.elasticsearch_app.model.Product.WarehouseLocation;

@Component
public class ProductMapper {

    public Product toEntity(ProductRequest request) {
        if (request == null)
            return null;

        return Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .sku(request.getSku())
                .category(request.getCategory())
                .subCategory(request.getSubCategory())
                .brand(request.getBrand())
                .price(request.getPrice())
                .originalPrice(request.getOriginalPrice())
                .stockQuantity(request.getStockQuantity())
                .inStock(request.getStockQuantity() > 0)
                .isActive(request.getIsActive())
                .rating(request.getRating())
                .reviewCount(request.getReviewCount())
                .tags(request.getTags())
                .colors(request.getColors())
                .sizes(request.getSizes())
                .specifications(request.getSpecifications())
                .variants(mapVariantsToEntity(request.getVariants()))
                .warehouseLocation(parseGeoLocation(request.getWarehouseLocation()))
                .shippingCountries(request.getShippingCountries())
                .salesCount(request.getSalesCount())
                .searchSuggest(request.getName()) // For autocomplete features
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private WarehouseLocation parseGeoLocation(String locationStr) {
        if (locationStr == null || !locationStr.contains(","))
            return null;
        try {
            String[] parts = locationStr.split(",");
            double lat = Double.parseDouble(parts[0].trim());
            double lon = Double.parseDouble(parts[1].trim());

            // Return your domain model's inner GeoLocation class
            return WarehouseLocation.builder()
                    .lat(lat)
                    .lon(lon)
                    .build();
        } catch (NumberFormatException e) {
            // Log the exception in production environments
            return null;
        }
    }

    private List<Product.ProductVariant> mapVariantsToEntity(List<ProductRequest.VariantRequest> requests) {
        if (requests == null)
            return Collections.emptyList();
        return requests.stream().map(v -> Product.ProductVariant.builder()
                .variantId(v.getVariantId())
                .color(v.getColor())
                .size(v.getSize())
                .price(v.getPrice())
                .stock(v.getStock())
                .sku(v.getSku())
                .build()).toList();
    }

    public ProductResponse toResponse(Product entity) {
        if (entity == null)
            return null;

        return ProductResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .sku(entity.getSku())
                .category(entity.getCategory())
                .subCategory(entity.getSubCategory())
                .brand(entity.getBrand())
                .price(entity.getPrice())
                .originalPrice(entity.getOriginalPrice())
                .discountPercentage(calculateDiscount(entity.getPrice(), entity.getOriginalPrice()))
                .stockQuantity(entity.getStockQuantity())
                .inStock(entity.getInStock())
                .rating(entity.getRating())
                .reviewCount(entity.getReviewCount())
                .tags(entity.getTags())
                .colors(entity.getColors())
                .sizes(entity.getSizes())
                .specifications(entity.getSpecifications())
                .variants(mapVariantsToResponse(entity.getVariants()))
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private List<ProductResponse.VariantResponse> mapVariantsToResponse(List<Product.ProductVariant> variants) {
        if (variants == null)
            return Collections.emptyList();
        return variants.stream().map(v -> ProductResponse.VariantResponse.builder()
                .variantId(v.getVariantId())
                .color(v.getColor())
                .size(v.getSize())
                .price(v.getPrice())
                .stock(v.getStock())
                .sku(v.getSku())
                .build()).toList();
    }

    private Integer calculateDiscount(BigDecimal current, BigDecimal original) {
        if (current == null || original == null || original.compareTo(BigDecimal.ZERO) <= 0) {
            return 0;
        }
        return original.subtract(current)
                .divide(original, 2, java.math.RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .intValue();
    }
}
