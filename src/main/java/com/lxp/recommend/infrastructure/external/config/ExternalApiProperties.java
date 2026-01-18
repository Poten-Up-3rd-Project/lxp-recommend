package com.lxp.recommend.infrastructure.external.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 외부 BC API 설정
 */
@Component
@ConfigurationProperties(prefix = "external")
@Getter
@Setter
public class ExternalApiProperties {

    private ServiceConfig member = new ServiceConfig();
    private ServiceConfig course = new ServiceConfig();
    private ServiceConfig enrollment = new ServiceConfig();
    private TimeoutConfig timeout = new TimeoutConfig();

    @Getter
    @Setter
    public static class ServiceConfig {
        private String baseUrl;
    }

    @Getter
    @Setter
    public static class TimeoutConfig {
        private int connect = 3000;
        private int read = 5000;
    }
}
