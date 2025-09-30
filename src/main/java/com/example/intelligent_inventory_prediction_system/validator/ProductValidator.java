package com.example.intelligent_inventory_prediction_system.validator;

import com.example.intelligent_inventory_prediction_system.dto.request.ProductRequestDTO;
import com.example.intelligent_inventory_prediction_system.model.ProductStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import static com.example.intelligent_inventory_prediction_system.constants.ProductConstants.*;

@Service
public class ProductValidator {

    public void validateCreateRequest(ProductRequestDTO request) {
        validateNotNull(request);
        validateName(request.getName());
        validatePrice(request.getPrice());
        validateDescription(request.getDescription());
        validateCategory(request.getCategory());
    }

    public void validateUpdateRequest(ProductRequestDTO request) {
        validateCreateRequest(request);
    }

    public void validateStatus(ProductStatus status) {
        if (status == null) {
            throw new IllegalArgumentException(PRODUCT_STATUS_NULL_MESSAGE);
        }
    }

    public void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Product ID must be a positive number");
        }
    }

    private void validateNotNull(ProductRequestDTO request) {
        if (request == null) {
            throw new IllegalArgumentException(PRODUCT_REQUEST_NULL_MESSAGE);
        }
    }

    private void validateName(String name) {
        if (!StringUtils.hasText(name)) {
            throw new IllegalArgumentException(PRODUCT_NAME_REQUIRED_MESSAGE);
        }
        if (name.length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException(
                    String.format(PRODUCT_NAME_TOO_LONG_MESSAGE, MAX_NAME_LENGTH)
            );
        }
    }

    private void validatePrice(Double price) {
        if (price != null && price < MIN_PRICE) {
            throw new IllegalArgumentException(PRODUCT_PRICE_NEGATIVE_MESSAGE);
        }
    }

    private void validateDescription(String description) {
        if (description != null && description.length() > MAX_DESCRIPTION_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("Product description cannot exceed %d characters", MAX_DESCRIPTION_LENGTH)
            );
        }
    }

    private void validateCategory(String category) {
        if (category != null && category.length() > MAX_CATEGORY_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("Product category cannot exceed %d characters", MAX_CATEGORY_LENGTH)
            );
        }
    }
}
