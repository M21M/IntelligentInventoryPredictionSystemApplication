package com.example.intelligent_inventory_prediction_system.repository;

import com.example.intelligent_inventory_prediction_system.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;


public interface InventoryRepository extends JpaRepository<Inventory, Long> {
}
