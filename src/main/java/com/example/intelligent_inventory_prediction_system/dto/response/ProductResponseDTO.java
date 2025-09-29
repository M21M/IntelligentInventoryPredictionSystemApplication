package com.example.intelligent_inventory_prediction_system.dto.response;
import com.example.intelligent_inventory_prediction_system.model.ProductStatus;
import lombok.Data;

@Data
public class ProductResponseDTO {
    private Long id;
    private String name;
    private String category;
    private String description;
    private Double price;
    private ProductStatus status;
}
