package com.criel.edove.user.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 身份码配置类
 */
@Data
@Configuration
@ConfigurationProperties("edove.identity-barcode")
public class BarcodeProperties {

    // 条形码图片宽度
    private int width;

    // 条形码图片高度
    private int height;

    // 身份码过期时间（单位：分钟）
    private int ttl;

    // 密钥
    private String key;
}
