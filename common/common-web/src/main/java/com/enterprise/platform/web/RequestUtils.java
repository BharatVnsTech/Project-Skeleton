package com.enterprise.platform.web;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public final class RequestUtils {

    private RequestUtils() {
    }

    public static String getCorrelationId() {
        String correlationId = getHeader(com.enterprise.platform.core.HttpHeaders.CORRELATION_ID);
        if (correlationId == null || correlationId.isBlank()) {
            return com.enterprise.platform.core.CorrelationIdGenerator.generate();
        }
        return correlationId;
    }

    public static String getHeader(String headerName) {
        ServletRequestAttributes attrs =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs != null) {
            HttpServletRequest request = attrs.getRequest();
            return request.getHeader(headerName);
        }
        return null;
    }
}
