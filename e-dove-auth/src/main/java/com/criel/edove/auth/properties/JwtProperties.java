package com.criel.edove.auth.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * jwt配置类：读取配置文件中的jwt相关配置
 */
@Data
@Configuration
@ConfigurationProperties("edove.jwt")
public class JwtProperties {

    // access-token密钥
    private String accessKey;

    // access-token过期时间（毫秒）
    private long accessTtl;

    // refresh-token密钥
    private String refreshKey;

    // refresh-token过期时间（毫秒）
    private long refreshTtl;
}
