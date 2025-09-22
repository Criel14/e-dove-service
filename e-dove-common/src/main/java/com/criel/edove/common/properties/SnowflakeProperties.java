package com.criel.edove.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 雪花算法配置类：读取配置文件中的雪花算法相关配置
 */
@Data
@Configuration
@ConfigurationProperties("edove.snowflake")
public class SnowflakeProperties {

    // 数据中心
    private long dataCenterId;

    // 机器标识
    private long workerId;
}
