package com.example.intelligent_inventory_prediction_system.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Product product;

    private Integer currentStock;
    private LocalDateTime lastUpdated;

    private Integer predicatedDemand;
    private Double predictedDemandConfidence;

    @OneToMany(mappedBy = "inventory")
    private List<InventoryPrediction> predictions;

}
