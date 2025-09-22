package com.example.intelligent_inventory_prediction_system.dto.request;
import com.example.intelligent_inventory_prediction_system.model.ProductStatus;
import lombok.Data;

@Data
public class ProductRequestDTO {
    private String name;
    private String description;
    private String category;
    private double price;
    private ProductStatus status;
}
