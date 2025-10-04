package com.criel.edove.auth.config;

import com.criel.edove.auth.mapper.UserAuthMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

/**
 * 数据库连接预热
 */
@Configuration
@RequiredArgsConstructor
public class DataSourceConfig {

    private final Logger LOGGER = LoggerFactory.getLogger(DataSourceConfig.class);

    private final UserAuthMapper userAuthMapper;

    /**
     * 预热数据库连接池 + 预热MyBatis
     */
    @PostConstruct
    public void preInitializeConnectionPool() {
        userAuthMapper.selectById(1L);
        LOGGER.info("数据库连接预热已完成");
    }
}
