package com.example.intelligent_inventory_prediction_system.dto.search;

import com.example.intelligent_inventory_prediction_system.model.ProductStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductSearchCriteria {
    private String keyword;
    private String name;
    private String category;
    private ProductStatus status;
    private Double minPrice;
    private Double maxPrice;
    private Boolean availability;
    private Boolean active;
}
