package com.enterprise.platform.gateway.filter;

import com.enterprise.platform.core.CorrelationConstants;
import com.enterprise.platform.core.CorrelationIdGenerator;
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
public class CorrelationIdFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(CorrelationIdFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        String correlationId = request.getHeaders().getFirst(CorrelationConstants.CORRELATION_ID_HEADER);
        if (correlationId == null || correlationId.isBlank()) {
            correlationId = CorrelationIdGenerator.generate();
        }

        final String finalCorrelationId = correlationId;

        ServerHttpRequest modifiedRequest = request.mutate()
                .header(CorrelationConstants.CORRELATION_ID_HEADER, finalCorrelationId)
                .build();

        log.info("Gateway request: method={}, path={}, correlationId={}",
                request.getMethod(), request.getURI().getPath(), finalCorrelationId);

        return chain.filter(exchange.mutate().request(modifiedRequest).build())
                .then(Mono.fromRunnable(() -> {
                    log.info("Gateway response: status={}, correlationId={}",
                            exchange.getResponse().getStatusCode(), finalCorrelationId);
                }));
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
