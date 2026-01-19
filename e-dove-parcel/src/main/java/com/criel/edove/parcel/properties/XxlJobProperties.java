package com.criel.edove.parcel.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties("xxl.job")
public class XxlJobProperties {

    private Admin admin = new Admin();
    private Executor executor = new Executor();

    @Data
    public static class Admin {
        private String addresses;
        private String accessToken;
        private int timeout;
    }

    @Data
    public static class Executor {
        private Boolean enabled;
        private String appname;
        private String address;
        private String ip;
        private int port;
        private String logpath;
        private int logretentiondays;
        private String excludedpackage;
    }
}

