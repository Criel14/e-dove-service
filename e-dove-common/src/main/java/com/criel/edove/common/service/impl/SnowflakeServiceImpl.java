package com.criel.edove.common.service.impl;

import cn.hutool.core.util.IdUtil;
import com.criel.edove.common.properties.SnowflakeProperties;
import com.criel.edove.common.service.SnowflakeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 雪花算法生成ID
 */
@Service
@RequiredArgsConstructor
public class SnowflakeServiceImpl implements SnowflakeService {

    // 雪花算法配置：数据中心和机器ID
    private final SnowflakeProperties snowflakeProperties;

    /**
     * 用Hutool工具生成雪花算法id
     * @return 返回生成的雪花算法id
     */
    @Override
    public long nextId() {
        return IdUtil.getSnowflake(snowflakeProperties.getWorkerId(), snowflakeProperties.getDataCenterId()).nextId();
    }
}
