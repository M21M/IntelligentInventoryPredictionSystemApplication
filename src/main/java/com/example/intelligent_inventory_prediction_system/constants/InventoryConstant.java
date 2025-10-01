package com.example.intelligent_inventory_prediction_system.constants;

public final class InventoryConstant {

    public static final int MIN_STOCK = 0;
    public static final int MAX_STOCK = 1_000_000;

    public static final String INVENTORY_NOT_FOUND_MESSAGE = "Inventory not found with id: ";
    public static final String INVENTORY_REQUEST_NULL_MESSAGE = "Inventory request cannot be null";
    public static final String PRODUCT_ID_NULL_MESSAGE = "Product ID cannot be null";
    public static final String PRODUCT_ID_INVALID_MESSAGE = "Product ID must be a positive number";
    public static final String STOCK_LEVEL_NULL_MESSAGE = "Stock level cannot be null";
    public static final String STOCK_LEVEL_NEGATIVE_MESSAGE = "Stock level cannot be negative";
    public static final String STOCK_LEVEL_EXCEEDS_MAX_MESSAGE = "Stock level exceeds maximum allowed: " + MAX_STOCK;

    private InventoryConstant() {
        throw new UnsupportedOperationException("Utility class");
    }
}
