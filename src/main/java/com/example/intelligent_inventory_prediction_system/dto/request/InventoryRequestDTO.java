package com.example.intelligent_inventory_prediction_system.dto.request;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class InventoryRequestDTO {
    private Long productId;
    private Integer currentStock;
    private LocalDateTime lastUpdated;

}
