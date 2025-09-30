package com.example.intelligent_inventory_prediction_system.validator;

import com.example.intelligent_inventory_prediction_system.dto.request.ProductRequestDTO;
import com.example.intelligent_inventory_prediction_system.model.ProductStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static com.example.intelligent_inventory_prediction_system.constants.ProductConstants.*;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("ProductValidator Tests")
class ProductValidatorTest {

    private ProductValidator productValidator;
    private TestDataFactory testDataFactory;

    @BeforeEach
    void setUp() {
        productValidator = new ProductValidator();
        testDataFactory = new TestDataFactory();
    }

    @Nested
    @DisplayName("Create Request Validation Tests")
    class CreateRequestValidationTests {

        @Test
        @DisplayName("Should validate successfully with valid create request")
        void validateCreateRequest_withValidRequest_shouldNotThrowException() {
            var validRequest = testDataFactory.createValidProductRequest();

            assertThatNoException()
                    .isThrownBy(() -> productValidator.validateCreateRequest(validRequest));
        }

        @Test
        @DisplayName("Should throw exception when create request is null")
        void validateCreateRequest_withNullRequest_shouldThrowException() {
            assertThatThrownBy(() -> productValidator.validateCreateRequest(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage(PRODUCT_REQUEST_NULL_MESSAGE);
        }

        @Test
        @DisplayName("Should throw exception when name is missing")
        void validateCreateRequest_withMissingName_shouldThrowException() {
            var request = testDataFactory.createProductRequestWithName(null);

            assertThatThrownBy(() -> productValidator.validateCreateRequest(request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage(PRODUCT_NAME_REQUIRED_MESSAGE);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   ", "\t", "\n"})
        @DisplayName("Should throw exception when name is blank")
        void validateCreateRequest_withBlankName_shouldThrowException(String name) {
            var request = testDataFactory.createProductRequestWithName(name);

            assertThatThrownBy(() -> productValidator.validateCreateRequest(request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage(PRODUCT_NAME_REQUIRED_MESSAGE);
        }

        @Test
        @DisplayName("Should throw exception when name exceeds maximum length")
        void validateCreateRequest_withTooLongName_shouldThrowException() {
            var longName = "a".repeat(MAX_NAME_LENGTH + 1);
            var request = testDataFactory.createProductRequestWithName(longName);

            assertThatThrownBy(() -> productValidator.validateCreateRequest(request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage(String.format(PRODUCT_NAME_TOO_LONG_MESSAGE, MAX_NAME_LENGTH));
        }

        @Test
        @DisplayName("Should validate successfully when name is at maximum length")
        void validateCreateRequest_withMaxLengthName_shouldNotThrowException() {
            var maxLengthName = "a".repeat(MAX_NAME_LENGTH);
            var request = testDataFactory.createProductRequestWithName(maxLengthName);

            assertThatNoException()
                    .isThrownBy(() -> productValidator.validateCreateRequest(request));
        }

        @Test
        @DisplayName("Should throw exception when price is negative")
        void validateCreateRequest_withNegativePrice_shouldThrowException() {
            var request = testDataFactory.createProductRequestWithPrice(-10.0);

            assertThatThrownBy(() -> productValidator.validateCreateRequest(request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage(PRODUCT_PRICE_NEGATIVE_MESSAGE);
        }

        @Test
        @DisplayName("Should validate successfully when price is zero")
        void validateCreateRequest_withZeroPrice_shouldNotThrowException() {
            var request = testDataFactory.createProductRequestWithPrice(0.0);

            assertThatNoException()
                    .isThrownBy(() -> productValidator.validateCreateRequest(request));
        }

        @Test
        @DisplayName("Should validate successfully when price is null")
        void validateCreateRequest_withNullPrice_shouldNotThrowException() {
            var request = testDataFactory.createProductRequestWithPrice(null);

            assertThatNoException()
                    .isThrownBy(() -> productValidator.validateCreateRequest(request));
        }

        @Test
        @DisplayName("Should throw exception when description exceeds maximum length")
        void validateCreateRequest_withTooLongDescription_shouldThrowException() {
            var longDescription = "a".repeat(MAX_DESCRIPTION_LENGTH + 1);
            var request = testDataFactory.createProductRequestWithDescription(longDescription);

            assertThatThrownBy(() -> productValidator.validateCreateRequest(request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Product description cannot exceed")
                    .hasMessageContaining(String.valueOf(MAX_DESCRIPTION_LENGTH));
        }

        @Test
        @DisplayName("Should validate successfully when description is at maximum length")
        void validateCreateRequest_withMaxLengthDescription_shouldNotThrowException() {
            var maxLengthDescription = "a".repeat(MAX_DESCRIPTION_LENGTH);
            var request = testDataFactory.createProductRequestWithDescription(maxLengthDescription);

            assertThatNoException()
                    .isThrownBy(() -> productValidator.validateCreateRequest(request));
        }

        @Test
        @DisplayName("Should validate successfully when description is null")
        void validateCreateRequest_withNullDescription_shouldNotThrowException() {
            var request = testDataFactory.createProductRequestWithDescription(null);

            assertThatNoException()
                    .isThrownBy(() -> productValidator.validateCreateRequest(request));
        }

        @Test
        @DisplayName("Should throw exception when category exceeds maximum length")
        void validateCreateRequest_withTooLongCategory_shouldThrowException() {
            var longCategory = "a".repeat(MAX_CATEGORY_LENGTH + 1);
            var request = testDataFactory.createProductRequestWithCategory(longCategory);

            assertThatThrownBy(() -> productValidator.validateCreateRequest(request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Product category cannot exceed")
                    .hasMessageContaining(String.valueOf(MAX_CATEGORY_LENGTH));
        }

        @Test
        @DisplayName("Should validate successfully when category is at maximum length")
        void validateCreateRequest_withMaxLengthCategory_shouldNotThrowException() {
            var maxLengthCategory = "a".repeat(MAX_CATEGORY_LENGTH);
            var request = testDataFactory.createProductRequestWithCategory(maxLengthCategory);

            assertThatNoException()
                    .isThrownBy(() -> productValidator.validateCreateRequest(request));
        }

        @Test
        @DisplayName("Should validate successfully when category is null")
        void validateCreateRequest_withNullCategory_shouldNotThrowException() {
            var request = testDataFactory.createProductRequestWithCategory(null);

            assertThatNoException()
                    .isThrownBy(() -> productValidator.validateCreateRequest(request));
        }
    }

    @Nested
    @DisplayName("Update Request Validation Tests")
    class UpdateRequestValidationTests {

        @Test
        @DisplayName("Should validate successfully with valid update request")
        void validateUpdateRequest_withValidRequest_shouldNotThrowException() {
            var validRequest = testDataFactory.createValidProductRequest();

            assertThatNoException()
                    .isThrownBy(() -> productValidator.validateUpdateRequest(validRequest));
        }

        @Test
        @DisplayName("Should throw exception when update request is null")
        void validateUpdateRequest_withNullRequest_shouldThrowException() {
            assertThatThrownBy(() -> productValidator.validateUpdateRequest(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage(PRODUCT_REQUEST_NULL_MESSAGE);
        }

        @Test
        @DisplayName("Should apply same validation rules as create request")
        void validateUpdateRequest_shouldApplySameRulesAsCreateRequest() {
            var invalidRequest = testDataFactory.createProductRequestWithPrice(-10.0);

            assertThatThrownBy(() -> productValidator.validateUpdateRequest(invalidRequest))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage(PRODUCT_PRICE_NEGATIVE_MESSAGE);
        }
    }

    @Nested
    @DisplayName("Status Validation Tests")
    class StatusValidationTests {

        @Test
        @DisplayName("Should validate successfully with valid status")
        void validateStatus_withValidStatus_shouldNotThrowException() {
            assertThatNoException()
                    .isThrownBy(() -> productValidator.validateStatus(ProductStatus.AVAILABLE));
        }

        @Test
        @DisplayName("Should validate successfully with all status values")
        void validateStatus_withAllStatusValues_shouldNotThrowException() {
            for (ProductStatus status : ProductStatus.values()) {
                assertThatNoException()
                        .isThrownBy(() -> productValidator.validateStatus(status));
            }
        }

        @Test
        @DisplayName("Should throw exception when status is null")
        void validateStatus_withNullStatus_shouldThrowException() {
            assertThatThrownBy(() -> productValidator.validateStatus(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage(PRODUCT_STATUS_NULL_MESSAGE);
        }
    }

    @Nested
    @DisplayName("ID Validation Tests")
    class IdValidationTests {

        @Test
        @DisplayName("Should validate successfully with valid positive ID")
        void validateId_withValidPositiveId_shouldNotThrowException() {
            assertThatNoException()
                    .isThrownBy(() -> productValidator.validateId(1L));
        }

        @ParameterizedTest
        @ValueSource(longs = {1L, 10L, 100L, 1000L, Long.MAX_VALUE})
        @DisplayName("Should validate successfully with various positive IDs")
        void validateId_withVariousPositiveIds_shouldNotThrowException(Long id) {
            assertThatNoException()
                    .isThrownBy(() -> productValidator.validateId(id));
        }

        @Test
        @DisplayName("Should throw exception when ID is null")
        void validateId_withNullId_shouldThrowException() {
            assertThatThrownBy(() -> productValidator.validateId(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Product ID must be a positive number");
        }

        @Test
        @DisplayName("Should throw exception when ID is zero")
        void validateId_withZeroId_shouldThrowException() {
            assertThatThrownBy(() -> productValidator.validateId(0L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Product ID must be a positive number");
        }

        @ParameterizedTest
        @ValueSource(longs = {-1L, -10L, -100L, Long.MIN_VALUE})
        @DisplayName("Should throw exception when ID is negative")
        void validateId_withNegativeId_shouldThrowException(Long id) {
            assertThatThrownBy(() -> productValidator.validateId(id))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Product ID must be a positive number");
        }
    }

    private static class TestDataFactory {

        ProductRequestDTO createValidProductRequest() {
            var request = new ProductRequestDTO();
            request.setName("Test Product");
            request.setCategory("Test Category");
            request.setDescription("Test Description");
            request.setPrice(100.0);
            request.setStatus(ProductStatus.AVAILABLE);
            return request;
        }

        ProductRequestDTO createProductRequestWithName(String name) {
            var request = createValidProductRequest();
            request.setName(name);
            return request;
        }

        ProductRequestDTO createProductRequestWithPrice(Double price) {
            var request = createValidProductRequest();
            request.setPrice(price);
            return request;
        }

        ProductRequestDTO createProductRequestWithDescription(String description) {
            var request = createValidProductRequest();
            request.setDescription(description);
            return request;
        }

        ProductRequestDTO createProductRequestWithCategory(String category) {
            var request = createValidProductRequest();
            request.setCategory(category);
            return request;
        }
    }
}
