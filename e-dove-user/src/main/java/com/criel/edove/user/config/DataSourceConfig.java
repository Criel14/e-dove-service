package com.criel.edove.user.config;

import com.criel.edove.user.mapper.UserInfoMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 数据库连接预热
 */
@Configuration
@RequiredArgsConstructor
public class DataSourceConfig {

    private final Logger LOGGER = LoggerFactory.getLogger(DataSourceConfig.class);

    private final UserInfoMapper userInfoMapper;

    /**
     * 预热数据库连接池 + 预热MyBatis
     */
    @PostConstruct
    public void preInitializeConnectionPool() {
        userInfoMapper.selectById(1L);
        LOGGER.info("数据库连接预热已完成");
    }
}
