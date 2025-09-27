package com.criel.edove.gateway.filter.global;

import com.criel.edove.gateway.context.UserInfoContext;
import com.criel.edove.gateway.properties.EDoveUriProperties;
import com.criel.edove.gateway.result.Result;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * jwt校验全局过滤器
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final WebClient webClient;

    // 接口地址配置
    private final EDoveUriProperties eDoveUriProperties;

    // 过滤的url
    private static final String[] FILTER_URL = {"/sign-in", "/register", "/refresh", "/otp"};

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 过滤请求：登录、注册、refresh token
        String path = exchange.getRequest().getURI().getPath();
        for (String url : FILTER_URL) {
            if (path.contains(url)) {
                return chain.filter(exchange);
            }
        }

        // 获取用户的token
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null) {
            // 返回401并中断请求
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        String token = authHeader.split("\\s+")[1];

        String authUri = eDoveUriProperties.getJwtAuth() + "?accessToken=" + token;
        // 调用接口校验jwt并获取用户信息
        return webClient.get()
                .uri(authUri)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Result<UserInfoContext>>() {
                })
                .flatMap(result -> {
                    // 获取用户信息后放入请求头中
                    if (result.getStatus() && result.getData() != null) {
                        UserInfoContext userInfoContext = result.getData();
                        ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                                .header("X-User-Id", Long.toString(userInfoContext.getUserId()))
                                .header("X-Username", userInfoContext.getUsername())
                                .header("X-Phone", userInfoContext.getPhone())
                                .build();

                        return chain.filter(exchange.mutate()
                                .request(modifiedRequest)
                                .build()
                        );
                    } else {
                        // 校验jwt失败返回401
                        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                        return exchange.getResponse().setComplete();
                    }
                })
                .onErrorResume(e -> {
                    // 调用失败返回500错误
                    LOGGER.error("调用jwt校验接口失败", e);
                    exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
                    return exchange.getResponse().setComplete();
                });
    }

    @Override
    public int getOrder() {
        return 0;
    }
}