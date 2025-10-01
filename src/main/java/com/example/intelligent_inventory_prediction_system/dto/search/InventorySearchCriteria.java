package com.example.intelligent_inventory_prediction_system.dto.search;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventorySearchCriteria {
    private Long productId;
    private Integer minStock;
    private Integer maxStock;
}
