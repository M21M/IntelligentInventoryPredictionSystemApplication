package com.example.intelligent_inventory_prediction_system.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;


@Entity
@Data
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private Integer currentStock;
    private LocalDateTime lastUpdated;

}
