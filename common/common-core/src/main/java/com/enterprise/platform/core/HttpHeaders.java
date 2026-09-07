package com.enterprise.platform.core;

public final class HttpHeaders {

    private HttpHeaders() {
    }

    public static final String CORRELATION_ID = "X-Correlation-ID";
    public static final String SERVICE_NAME = "X-Service-Name";
    public static final String REQUEST_DURATION = "X-Request-Duration";
    public static final String AUTHORIZATION = "Authorization";
    public static final String CONTENT_TYPE = "Content-Type";
}
