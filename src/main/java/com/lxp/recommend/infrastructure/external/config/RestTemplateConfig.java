package com.lxp.recommend.infrastructure.external.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

/**
 * 외부 BC 호출용 RestTemplate 설정
 */
@Configuration
@RequiredArgsConstructor
public class RestTemplateConfig {

    private final ExternalApiProperties properties;

    @Bean(name = "externalApiRestTemplate")
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .setConnectTimeout(Duration.ofMillis(properties.getTimeout().getConnect()))
                .setReadTimeout(Duration.ofMillis(properties.getTimeout().getRead()))
                .requestFactory(this::clientHttpRequestFactory)
                .build();
    }

    private ClientHttpRequestFactory clientHttpRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(properties.getTimeout().getConnect());
        factory.setReadTimeout(properties.getTimeout().getRead());
        return factory;
    }
}
