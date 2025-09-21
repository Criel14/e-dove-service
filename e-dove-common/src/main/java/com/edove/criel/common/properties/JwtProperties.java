package com.edove.criel.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties("edove.jwt")
public class JwtProperties {

    // access-token密钥
    private String accessKey;

    // access-token过期时间（毫秒）
    private int accessTtl;

    // refresh-token密钥
    private String refreshKey;

    // refresh-token过期时间（毫秒）
    private int refreshTtl;
}
