package com.example.intelligent_inventory_prediction_system.model;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class InventoryPrediction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne()
    @JoinColumn(name = "inventory_id")
    private Inventory inventory;
    private Long predictedStock;
}
