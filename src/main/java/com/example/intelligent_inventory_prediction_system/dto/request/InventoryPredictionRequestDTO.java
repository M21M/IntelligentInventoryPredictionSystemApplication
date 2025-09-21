package com.example.intelligent_inventory_prediction_system.dto.request;

import lombok.Data;

@Data
public class InventoryPredictionRequestDTO {
    private Long inventoryId;
    private Long predictedStock;
}
