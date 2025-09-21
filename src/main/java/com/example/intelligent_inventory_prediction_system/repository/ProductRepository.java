package com.example.intelligent_inventory_prediction_system.repository;
import com.example.intelligent_inventory_prediction_system.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategoryIgnoreCase(String category);
    List<Product> findByNameIgnoreCase(String name);
}
