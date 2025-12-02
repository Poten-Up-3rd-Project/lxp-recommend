package com.lxp.application;

import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

@Configuration
public class OpenApiConfig {

    private final ResourceLoader resourceLoader;

    public OpenApiConfig(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Bean
    public OpenAPI customOpenAPI() {
        // openapi.yml 파일 로드
        Resource resource = resourceLoader.getResource("classpath:openapi.yml");

        try (Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
            String yamlContent = FileCopyUtils.copyToString(reader);

            // 문자열 내용을 기반으로 OpenAPI 객체를 생성하고 반환합니다.
            // Springdoc이 이 Bean을 사용하여 문서를 서비스합니다.
            return new io.swagger.v3.parser.OpenAPIV3Parser().readContents(yamlContent).getOpenAPI();

        } catch (IOException e) {
            // 파일 로드 실패 시 예외 처리
            throw new RuntimeException("Failed to load openapi.yml file", e);
        }

    }
}
