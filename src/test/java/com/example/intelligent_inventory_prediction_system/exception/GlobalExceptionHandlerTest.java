package com.example.intelligent_inventory_prediction_system.exception;

import com.example.intelligent_inventory_prediction_system.dto.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {
    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Mock
    private HttpServletRequest mockHttpServletRequest;

    @Mock
    private  WebRequest mockWebRequest;



    @BeforeEach
    void setUp() {
        when(mockWebRequest.getDescription(false)).thenReturn("uri=/api/test/endpoint");
//        ServletWebRequest servletWebRequest = mock(ServletWebRequest.class);
//        when(servletWebRequest.getRequest()).thenReturn(mockHttpServletRequest);
//        when(mockHttpServletRequest.getRequestURI()).thenReturn("/api/test/endpoint");
    }

    @Test
    void handleIllegalArgumentException_ShouldReturnBadRequest(){
        IllegalArgumentException illegalArgumentException = new IllegalArgumentException("Invalid ID");
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleIllegalArgumentException(illegalArgumentException, mockWebRequest);
        ErrorResponse errorResponseBody = response.getBody();
        assertThat(response.getStatusCodeValue()).isEqualTo(400);
        assertThat(errorResponseBody).isNotNull();
        assertThat(errorResponseBody.getError()).isEqualTo("Invalid Argument");
        assertThat(errorResponseBody.getMessage()).isEqualTo("Invalid ID");
        assertThat(errorResponseBody.getPath()).isEqualTo("/api/test/endpoint");
    }


}