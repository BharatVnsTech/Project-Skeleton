package com.enterprise.platform.user.interceptor;

import com.enterprise.platform.core.CorrelationConstants;
import com.enterprise.platform.logging.CorrelationIdContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class CorrelationIdInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(CorrelationIdInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String correlationId = request.getHeader(CorrelationConstants.CORRELATION_ID_HEADER);
        String resolvedId = CorrelationIdContext.getOrCreate(correlationId);
        response.setHeader(CorrelationConstants.CORRELATION_ID_HEADER, resolvedId);
        log.debug("Correlation ID resolved: {}", resolvedId);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        CorrelationIdContext.clear();
    }
}
