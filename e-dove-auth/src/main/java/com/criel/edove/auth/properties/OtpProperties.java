package com.criel.edove.auth.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 验证码配置类
 */
@Data
@Configuration
@ConfigurationProperties("edove.otp")
public class OtpProperties {

    // 过期时间
    private int ttl;

}
