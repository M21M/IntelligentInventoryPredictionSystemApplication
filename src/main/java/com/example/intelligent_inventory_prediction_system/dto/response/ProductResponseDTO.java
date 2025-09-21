package com.example.intelligent_inventory_prediction_system.dto.response;
import lombok.Data;

@Data
public class ProductResponseDTO {
    private Long Id;
    private String Name;
    private String Category;
    private double Price;
}
