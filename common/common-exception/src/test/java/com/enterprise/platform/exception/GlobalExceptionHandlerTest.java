package com.enterprise.platform.exception;

import com.enterprise.platform.web.ApiResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleResourceNotFound_shouldReturn404() {
        ResourceNotFoundException ex =
                new ResourceNotFoundException(ErrorCode.USER_NOT_FOUND, "User not found with id: 999");

        ResponseEntity<ApiResponse<Void>> response = handler.handleResourceNotFound(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        ApiResponse<Void> body = response.getBody();
        assertNotNull(body);
        assertFalse(body.isSuccess());
        assertEquals(ErrorCode.USER_NOT_FOUND.getCode(), body.getErrorCode());
        assertEquals("User not found with id: 999", body.getMessage());
    }

    @Test
    void handleValidation_shouldReturn400() throws Exception {
        var mvcMethod = org.springframework.core.MethodParameter.forExecutable(
                Object.class.getMethod("toString"), -1);
        var result = new org.springframework.validation.BeanPropertyBindingResult(new Object(), "obj");
        result.addError(new org.springframework.validation.FieldError(
                "obj", "field", "must not be null"));
        var ex = new org.springframework.web.bind.MethodArgumentNotValidException(mvcMethod, result);

        ResponseEntity<ApiResponse<Void>> response = handler.handleValidation(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals(ErrorCode.VALIDATION_ERROR.getCode(), response.getBody().getErrorCode());
    }

    @Test
    void handleGenericException_shouldReturn500() {
        RuntimeException ex = new RuntimeException("Unexpected error");

        ResponseEntity<ApiResponse<Void>> response = handler.handleGenericException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals(ErrorCode.GENERIC_ERROR.getCode(), response.getBody().getErrorCode());
    }
}
