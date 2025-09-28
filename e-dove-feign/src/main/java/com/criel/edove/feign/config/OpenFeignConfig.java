package com.criel.edove.feign.config;

import com.criel.edove.common.context.UserInfoContext;
import com.criel.edove.common.context.UserInfoContextHolder;
import feign.Logger;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;

/**
 * OpenFeign 配置类
 */
public class OpenFeignConfig {
    /**
     * 配置日志级别
     */
    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    /**
     * 远程调用时传递用户信息
     */
    @Bean
    public RequestInterceptor userInfoRequestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                UserInfoContext userInfoContext = UserInfoContextHolder.getUserInfoContext();
                if (userInfoContext.getUserId() != null) {
                    template.header("X-User-Id", String.valueOf(userInfoContext.getUserId()));
                }
                if (userInfoContext.getUsername() != null) {
                    template.header("X-Username", userInfoContext.getUsername());
                }
                if (userInfoContext.getPhone() != null) {
                    template.header("X-Phone", userInfoContext.getPhone());
                }
            }
        };
    }
}
