package com.example.intelligent_inventory_prediction_system.dto.response;

import com.example.intelligent_inventory_prediction_system.model.Product;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class InventoryResponseDTO {
    private Long id;
    private Long productId;
    private Integer currentStock;
    private LocalDateTime lastUpdated;


}
