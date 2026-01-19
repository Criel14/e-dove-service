package com.criel.edove.parcel.config;

import com.criel.edove.parcel.properties.XxlJobProperties;
import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * xxl-job 配置类
 */
@Configuration
@RequiredArgsConstructor
public class XxlJobConfig {

    private static final Logger logger = LoggerFactory.getLogger(XxlJobConfig.class);

    private final XxlJobProperties xxlJobProperties;


    @Bean
    public XxlJobSpringExecutor xxlJobExecutor() {
        logger.info(">>>>>>>>>>> xxl-job config init.");
        XxlJobSpringExecutor xxlJobSpringExecutor = new XxlJobSpringExecutor();
        xxlJobSpringExecutor.setAdminAddresses(xxlJobProperties.getAdmin().getAddresses());
        xxlJobSpringExecutor.setAccessToken(xxlJobProperties.getAdmin().getAccessToken());
        xxlJobSpringExecutor.setTimeout(xxlJobProperties.getAdmin().getTimeout());
        xxlJobSpringExecutor.setEnabled(xxlJobProperties.getExecutor().getEnabled());
        xxlJobSpringExecutor.setAppname(xxlJobProperties.getExecutor().getAppname());
        xxlJobSpringExecutor.setAddress(xxlJobProperties.getExecutor().getAddress());
        xxlJobSpringExecutor.setIp(xxlJobProperties.getExecutor().getIp());
        xxlJobSpringExecutor.setPort(xxlJobProperties.getExecutor().getPort());
        xxlJobSpringExecutor.setLogPath(xxlJobProperties.getExecutor().getLogpath());
        xxlJobSpringExecutor.setLogRetentionDays(xxlJobProperties.getExecutor().getLogretentiondays());
        xxlJobSpringExecutor.setExcludedPackage(xxlJobProperties.getExecutor().getExcludedpackage());

        return xxlJobSpringExecutor;
    }

}
