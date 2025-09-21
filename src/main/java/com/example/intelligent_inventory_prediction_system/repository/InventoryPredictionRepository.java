package com.example.intelligent_inventory_prediction_system.repository;

import com.example.intelligent_inventory_prediction_system.model.InventoryPrediction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryPredictionRepository extends JpaRepository<InventoryPrediction, Long> {
}
