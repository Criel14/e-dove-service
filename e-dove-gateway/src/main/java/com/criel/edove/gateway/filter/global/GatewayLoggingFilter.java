package com.criel.edove.gateway.filter.global;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Collections;
import java.util.Set;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.*;


@Component
public class GatewayLoggingFilter implements GlobalFilter, Ordered {

    private static final Logger LOGGER = LoggerFactory.getLogger(GatewayLoggingFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        long startTime = System.currentTimeMillis();

        // 获取客户端原始请求信息
        ServerHttpRequest request = exchange.getRequest();
        String clientIp = getClientIp(request);
        String httpMethod = request.getMethod().name();
        String userAgent = request.getHeaders().getFirst("User-Agent");

        // 获取原始请求URL（用户实际访问的地址）
        String originalUrl = getOriginalRequestUrl(exchange);

        LOGGER.info("客户端请求 → IP: {}, 方法: {}, 地址: {}, User-Agent: {}",
                clientIp, httpMethod, originalUrl, userAgent);

        // 执行过滤链，在响应时打印转发信息
        return chain.filter(exchange)
                .doFinally(signal -> {
                    long duration = System.currentTimeMillis() - startTime;
                    logForwardingInfo(exchange, duration);
                });
    }

    /**
     * 获取原始请求URL（用户实际访问的地址）
     */
    private String getOriginalRequestUrl(ServerWebExchange exchange) {
        Set<URI> originalUris = exchange.getAttributeOrDefault(GATEWAY_ORIGINAL_REQUEST_URL_ATTR, Collections.emptySet());

        if (!originalUris.isEmpty()) {
            return originalUris.iterator().next().toString();
        }

        // 如果原始URL属性为空，使用当前请求的URI（降级方案）
        ServerHttpRequest request = exchange.getRequest();
        return request.getURI().toString();
    }

    /**
     * 记录转发信息和响应结果
     */
    private void logForwardingInfo(ServerWebExchange exchange, long duration) {
        try {
            // 获取转发目标地址
            URI targetUri = exchange.getAttribute(GATEWAY_REQUEST_URL_ATTR);
            String targetUrl = "未知";

            if (targetUri != null) {
                targetUrl = targetUri.toString();
            } else {
                // 尝试从路由信息获取目标地址
                Route route = exchange.getAttribute(GATEWAY_ROUTE_ATTR);
                if (route != null && route.getUri() != null) {
                    targetUrl = route.getUri().toString();
                }
            }

            // 获取响应状态
            Integer responseStatus = exchange.getResponse().getStatusCode() != null ?
                    exchange.getResponse().getStatusCode().value() : null;

            String statusStr = responseStatus != null ? String.valueOf(responseStatus) : "未知";

            LOGGER.info("网关转发 ← 目标服务: {}, 响应状态: {}, 耗时: {}ms",
                    targetUrl, statusStr, duration);

        } catch (Exception e) {
            LOGGER.warn("记录转发信息时发生异常", e);
        }
    }

    /**
     * 获取客户端真实IP
     */
    private String getClientIp(ServerHttpRequest request) {
        String xForwardedFor = request.getHeaders().getFirst("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeaders().getFirst("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }

        return request.getRemoteAddress() != null ?
                request.getRemoteAddress().getAddress().getHostAddress() : "未知";
    }

    @Override
    public int getOrder() {
        // 设置较高的优先级，确保在其他过滤器之前执行
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
