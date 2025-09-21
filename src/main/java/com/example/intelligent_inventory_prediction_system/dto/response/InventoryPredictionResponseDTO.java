package com.example.intelligent_inventory_prediction_system.dto.response;
import com.example.intelligent_inventory_prediction_system.model.Inventory;
import lombok.Data;

@Data
public class InventoryPredictionResponseDTO {
    private Long id; 
    private Inventory inventory;
    private Long predictedStock;
}
