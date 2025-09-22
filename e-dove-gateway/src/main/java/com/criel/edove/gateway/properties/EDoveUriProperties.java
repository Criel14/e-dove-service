package com.criel.edove.gateway.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 网关调用接口地址配置
 */
@Data
@Configuration
@ConfigurationProperties("edove.uri")
public class EDoveUriProperties {

    private String jwtAuth;

}
