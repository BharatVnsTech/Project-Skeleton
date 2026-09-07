package com.enterprise.platform.gateway.filter;

import com.enterprise.platform.core.CorrelationConstants;
import com.enterprise.platform.logging.CorrelationIdContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class CorrelationIdLoggingFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(CorrelationIdLoggingFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String correlationId = request.getHeaders().getFirst(CorrelationConstants.CORRELATION_ID_HEADER);
        CorrelationIdContext.setCorrelationId(correlationId);

        log.debug("Correlation ID set for request: {}", correlationId);

        return chain.filter(exchange).then(Mono.fromRunnable(CorrelationIdContext::clear));
    }

    @Override
    public int getOrder() {
        return -3;
    }
}
