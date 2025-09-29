package com.example.intelligent_inventory_prediction_system.constants;

public final class ProductConstants {

    private ProductConstants() {

    }

    public static final double MIN_PRICE = 0.0;
    public static final int MAX_NAME_LENGTH = 255;
    public static final int MAX_DESCRIPTION_LENGTH = 1000;
    public static final int MAX_CATEGORY_LENGTH = 100;


    public static final String PRODUCT_NOT_FOUND_MESSAGE = "Product not found with id: ";
    public static final String PRODUCT_REQUEST_NULL_MESSAGE = "Product request cannot be null";
    public static final String PRODUCT_NAME_REQUIRED_MESSAGE = "Product name is required";
    public static final String PRODUCT_NAME_TOO_LONG_MESSAGE = "Product name cannot exceed %d characters";
    public static final String PRODUCT_PRICE_NEGATIVE_MESSAGE = "Product price cannot be negative";
    public static final String PRODUCT_STATUS_NULL_MESSAGE = "Product status cannot be null";
}
