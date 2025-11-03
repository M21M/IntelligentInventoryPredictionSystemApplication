package com.example.intelligent_inventory_prediction_system.exception;

import com.example.intelligent_inventory_prediction_system.dto.response.ErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GlobalExceptionHandler Unit Tests")
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Mock
    private WebRequest mockWebRequest;

    @BeforeEach
    void setUp() {
        when(mockWebRequest.getDescription(false)).thenReturn("uri=/api/test/endpoint");
    }

    @Test
    @DisplayName("handleIllegalArgumentException should return 400 with error details")
    void handleIllegalArgumentException_ShouldReturnBadRequest() {
        // Arrange
        IllegalArgumentException exception = new IllegalArgumentException("Invalid ID");

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler
                .handleIllegalArgumentException(exception, mockWebRequest);

        // Assert
        ErrorResponse errorResponseBody = response.getBody();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(errorResponseBody).isNotNull();
        assertThat(errorResponseBody.getStatus()).isEqualTo(400);
        assertThat(errorResponseBody.getError()).isEqualTo("Invalid Argument");
        assertThat(errorResponseBody.getMessage()).isEqualTo("Invalid ID");
        assertThat(errorResponseBody.getPath()).isEqualTo("/api/test/endpoint");
        assertThat(errorResponseBody.getTimestamp()).isNotNull();
    }

    @Test
    @DisplayName("handleValidationExceptions should return 400 with validation errors map")
    void handleValidationExceptions_ShouldReturnBadRequest() {
        // Arrange
        MethodArgumentNotValidException mockException = mock(MethodArgumentNotValidException.class);
        BindingResult mockBindingResult = mock(BindingResult.class);

        List<ObjectError> errors = new ArrayList<>();
        errors.add(new FieldError("product", "name", "must not be blank"));
        errors.add(new FieldError("product", "price", "must be positive"));

        when(mockException.getBindingResult()).thenReturn(mockBindingResult);
        when(mockBindingResult.getAllErrors()).thenReturn(errors);

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler
                .handleValidationExceptions(mockException, mockWebRequest);

        // Assert
        ErrorResponse errorResponseBody = response.getBody();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(errorResponseBody).isNotNull();
        assertThat(errorResponseBody.getStatus()).isEqualTo(400);
        assertThat(errorResponseBody.getError()).isEqualTo("Validation Failed");
        assertThat(errorResponseBody.getMessage()).isEqualTo("Input validation failed");
        assertThat(errorResponseBody.getPath()).isEqualTo("/api/test/endpoint");
        assertThat(errorResponseBody.getValidationErrors()).isNotNull();
        assertThat(errorResponseBody.getValidationErrors()).hasSize(2);
        assertThat(errorResponseBody.getValidationErrors()).containsKeys("name", "price");
        assertThat(errorResponseBody.getValidationErrors().get("name")).isEqualTo("must not be blank");
        assertThat(errorResponseBody.getValidationErrors().get("price")).isEqualTo("must be positive");
    }

    @Test
    @DisplayName("handleValidationException should return 400 with custom validation error")
    void handleValidationException_ShouldReturnBadRequest() {
        // Arrange
        ValidationException exception = new ValidationException("Custom validation error");

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler
                .handleValidationException(exception, mockWebRequest);

        // Assert
        ErrorResponse errorResponseBody = response.getBody();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(errorResponseBody).isNotNull();
        assertThat(errorResponseBody.getStatus()).isEqualTo(400);
        assertThat(errorResponseBody.getError()).isEqualTo("Validation Error");
        assertThat(errorResponseBody.getMessage()).isEqualTo("Custom validation error");
        assertThat(errorResponseBody.getPath()).isEqualTo("/api/test/endpoint");
        assertThat(errorResponseBody.getValidationErrors()).isNull();
    }

    @Test
    @DisplayName("handleResourceNotFoundException should return 404")
    void handleResourceNotFoundException_ShouldReturnNotFound() {
        // Arrange
        ResourceNotFoundException exception = new ResourceNotFoundException("Product with ID 123 not found");

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler
                .handleResourceNotFoundException(exception, mockWebRequest);

        // Assert
        ErrorResponse errorResponseBody = response.getBody();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(errorResponseBody).isNotNull();
        assertThat(errorResponseBody.getStatus()).isEqualTo(404);
        assertThat(errorResponseBody.getError()).isEqualTo("Resource Not Found");
        assertThat(errorResponseBody.getMessage()).isEqualTo("Product with ID 123 not found");
        assertThat(errorResponseBody.getPath()).isEqualTo("/api/test/endpoint");
        assertThat(errorResponseBody.getValidationErrors()).isNull();
    }

    @Test
    @DisplayName("handleHttpMessageNotReadable should return 400 for malformed JSON")
    void handleHttpMessageNotReadable_ShouldReturnBadRequest() {
        // Arrange
        HttpMessageNotReadableException exception = mock(HttpMessageNotReadableException.class);
        when(exception.getMessage()).thenReturn("JSON parse error: Unexpected character");

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler
                .handleHttpMessageNotReadable(exception, mockWebRequest);

        // Assert
        ErrorResponse errorResponseBody = response.getBody();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(errorResponseBody).isNotNull();
        assertThat(errorResponseBody.getStatus()).isEqualTo(400);
        assertThat(errorResponseBody.getError()).isEqualTo("Invalid Request Body");
        assertThat(errorResponseBody.getMessage()).isEqualTo("Malformed JSON or invalid data format");
        assertThat(errorResponseBody.getPath()).isEqualTo("/api/test/endpoint");
    }

    @Test
    @DisplayName("handleGenericException should return 500 for unexpected errors")
    void handleGenericException_ShouldReturnInternalServerError() {
        // Arrange
        Exception exception = new RuntimeException("Unexpected database error");

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler
                .handleGenericException(exception, mockWebRequest);

        // Assert
        ErrorResponse errorResponseBody = response.getBody();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(errorResponseBody).isNotNull();
        assertThat(errorResponseBody.getStatus()).isEqualTo(500);
        assertThat(errorResponseBody.getError()).isEqualTo("Internal Server Error");
        assertThat(errorResponseBody.getMessage()).isEqualTo("An unexpected error occurred");
        assertThat(errorResponseBody.getPath()).isEqualTo("/api/test/endpoint");
    }

    @Test
    @DisplayName("All error responses should include timestamp")
    void allErrorResponses_ShouldIncludeTimestamp() {
        // Arrange
        Exception exception = new Exception("Test error");

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler
                .handleGenericException(exception, mockWebRequest);

        // Assert
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTimestamp()).isNotNull();
        assertThat(response.getBody().getTimestamp()).isNotNull();
    }

    @Test
    @DisplayName("ValidationErrors should only exist for MethodArgumentNotValidException")
    void validationErrors_ShouldOnlyExistForValidationException() {
        // Test ResourceNotFoundException (should NOT have validationErrors)
        ResourceNotFoundException notFoundEx = new ResourceNotFoundException("Not found");
        ResponseEntity<ErrorResponse> notFoundResponse = globalExceptionHandler
                .handleResourceNotFoundException(notFoundEx, mockWebRequest);
        assertThat(notFoundResponse.getBody()).isNotNull();
        assertThat(notFoundResponse.getBody().getValidationErrors()).isNull();

        // Test generic Exception (should NOT have validationErrors)
        Exception genericEx = new Exception("Generic error");
        ResponseEntity<ErrorResponse> genericResponse = globalExceptionHandler
                .handleGenericException(genericEx, mockWebRequest);
        assertThat(genericResponse.getBody()).isNotNull();
        assertThat(genericResponse.getBody().getValidationErrors()).isNull();
    }

    @Test
    @DisplayName("Path extraction should work correctly from WebRequest")
    void pathExtraction_ShouldWorkCorrectly() {
        // Arrange
        when(mockWebRequest.getDescription(false)).thenReturn("uri=/api/products/search");
        ResourceNotFoundException exception = new ResourceNotFoundException("Product not found");

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler
                .handleResourceNotFoundException(exception, mockWebRequest);

        // Assert
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getPath()).isEqualTo("/api/products/search");
    }
}
