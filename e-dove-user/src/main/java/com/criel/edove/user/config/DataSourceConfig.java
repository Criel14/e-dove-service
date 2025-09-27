package com.criel.edove.user.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Configuration
@RequiredArgsConstructor
public class DataSourceConfig {

    private final Logger LOGGER = LoggerFactory.getLogger(DataSourceConfig.class);

    private final DataSource dataSource;

    @PostConstruct
    public void preInitializeConnectionPool() {
        // 应用启动时预初始化连接池
        try (Connection connection = dataSource.getConnection()) {
            // 简单查询预热连接池
            connection.createStatement().execute("SELECT 1");
        } catch (SQLException e) {
            // 处理异常
            LOGGER.error("连接池预热异常: {}", e.getMessage());
        }
    }
}
