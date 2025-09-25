package com.criel.edove.gateway.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.function.Predicate;

/**
 * WebClient配置类
 */
@Configuration
public class WebClientConfig {

    private final Logger LOGGER = LoggerFactory.getLogger(WebClientConfig.class);

    /**
     * 创建 WebClient 实例，并配置重试机制。
     * tip：@Primary 注解表示当有多个相同类型的 Bean 时，优先使用这个 Bean，覆盖掉原本自带的配置
     */
    @Bean(name = "customWebClient")
    @Primary
    public WebClient webClientBuilder() {
        return WebClient.builder()
                .filter((request, next) -> next.exchange(request)
                        .retryWhen(createRetrySpec()) // 配置重试机制
                        .doOnError(error -> LOGGER.info("请求失败: {}", error.getMessage()))
                )
                .build();
    }

    /**
     * 创建重试策略
     *
     * @return 配置好的 Retry 实例
     */
    private Retry createRetrySpec() {
        // 定义可重试的错误类型
        Predicate<Throwable> retryableErrors = throwable -> {
            if (throwable instanceof WebClientRequestException) {
                return true; // 网络请求异常需要重试
            }
            if (throwable instanceof WebClientResponseException) {
                int statusCode = ((WebClientResponseException) throwable).getStatusCode().value();
                return statusCode >= 500 && statusCode < 600; // 服务器错误（5xx）需要重试
            }
            return false; // 其他异常不重试
        };

        // 配置重试策略
        return Retry.backoff(3, Duration.ofSeconds(1)) // 最多重试 3 次，每次间隔 1 秒
                .filter(retryableErrors) // 仅对可重试的错误进行重试
                .jitter(0.3); // 添加 30% 的随机抖动，避免多个请求同时重试导致的“重试风暴”
    }
}

