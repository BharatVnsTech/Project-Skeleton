package com.enterprise.platform.core;

public final class CorrelationConstants {

    private CorrelationConstants() {
    }

    public static final String CORRELATION_ID_HEADER = "X-Correlation-ID";
    public static final String CORRELATION_ID_MDC_KEY = "correlationId";
    public static final String CORRELATION_ID_DEFAULT_VALUE = "N/A";
}
