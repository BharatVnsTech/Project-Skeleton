package com.enterprise.platform.web;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ApiResponseTest {

    @Test
    void success_shouldCreateSuccessResponse() {
        ApiResponse<String> response = ApiResponse.success("test-data", "Success", "corr-123");

        assertTrue(response.isSuccess());
        assertEquals("Success", response.getMessage());
        assertEquals("test-data", response.getData());
        assertEquals("corr-123", response.getCorrelationId());
        assertNotNull(response.getTimestamp());
    }

    @Test
    void error_shouldCreateErrorResponse() {
        ApiResponse<Void> response = ApiResponse.error("Not found", "NOT_FOUND", "corr-456");

        assertFalse(response.isSuccess());
        assertEquals("Not found", response.getMessage());
        assertEquals("NOT_FOUND", response.getErrorCode());
        assertEquals("corr-456", response.getCorrelationId());
        assertNull(response.getData());
    }

    @Test
    void success_withoutCorrelationId_shouldWork() {
        ApiResponse<String> response = ApiResponse.success("data", "msg");

        assertTrue(response.isSuccess());
        assertNull(response.getCorrelationId());
    }
}
