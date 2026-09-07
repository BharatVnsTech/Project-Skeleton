package com.enterprise.platform.logging;

import org.slf4j.MDC;

import java.util.UUID;

public final class CorrelationIdContext {

    private CorrelationIdContext() {
    }

    private static final String CORRELATION_ID_KEY = "correlationId";

    public static String getCorrelationId() {
        String id = MDC.get(CORRELATION_ID_KEY);
        return id != null ? id : "N/A";
    }

    public static void setCorrelationId(String correlationId) {
        if (correlationId != null && !correlationId.isBlank()) {
            MDC.put(CORRELATION_ID_KEY, correlationId);
        } else {
            MDC.put(CORRELATION_ID_KEY, UUID.randomUUID().toString());
        }
    }

    public static String getOrCreate(String correlationId) {
        if (correlationId == null || correlationId.isBlank()) {
            correlationId = UUID.randomUUID().toString();
        }
        setCorrelationId(correlationId);
        return correlationId;
    }

    public static void clear() {
        MDC.remove(CORRELATION_ID_KEY);
    }
}
