package com.example.intelligent_inventory_prediction_system.dto.response;
import com.example.intelligent_inventory_prediction_system.model.ProductStatus;
import lombok.Data;

@Data
public class ProductResponseDTO {
    private Long Id;
    private String Name;
    private String Category;
    private double Price;
    private ProductStatus Status;
}
