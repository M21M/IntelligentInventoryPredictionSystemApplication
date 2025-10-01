package com.example.intelligent_inventory_prediction_system.validator;

import com.example.intelligent_inventory_prediction_system.dto.request.InventoryRequestDTO;
import org.springframework.stereotype.Service;

import static com.example.intelligent_inventory_prediction_system.constants.InventoryConstant.*;

@Service
public class InventoryValidator {

    public void validateCreateRequest(InventoryRequestDTO request) {
        validateNotNull(request);
        validateProductId(request.getProductId());
        validateCurrentStock(request.getCurrentStock());
    }

    public void validateUpdateRequest(InventoryRequestDTO request) {
        validateNotNull(request);
        if (request.getCurrentStock() != null) {
            validateStockLevel(request.getCurrentStock());
        }
    }

    public void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Inventory ID must be a positive number");
        }
    }

    private void validateNotNull(InventoryRequestDTO request) {
        if (request == null) {
            throw new IllegalArgumentException(INVENTORY_REQUEST_NULL_MESSAGE);
        }
    }

    private void validateProductId(Long productId) {
        if (productId == null) {
            throw new IllegalArgumentException(PRODUCT_ID_NULL_MESSAGE);
        }
        if (productId <= 0) {
            throw new IllegalArgumentException(PRODUCT_ID_INVALID_MESSAGE);
        }
    }

    private void validateCurrentStock(Integer stock) {
        if (stock == null) {
            throw new IllegalArgumentException(STOCK_LEVEL_NULL_MESSAGE);
        }
        validateStockLevel(stock);
    }

    private void validateStockLevel(Integer stock) {
        if (stock < MIN_STOCK) {
            throw new IllegalArgumentException(STOCK_LEVEL_NEGATIVE_MESSAGE);
        }
        if (stock > MAX_STOCK) {
            throw new IllegalArgumentException(STOCK_LEVEL_EXCEEDS_MAX_MESSAGE);
        }
    }
}
