package com.lxp.application;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.parser.OpenAPIV3Parser;
import io.swagger.v3.parser.core.models.ParseOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.FileCopyUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Configuration
public class OpenApiConfig {

    private final ResourcePatternResolver resourcePatternResolver;

    public OpenApiConfig(ResourcePatternResolver resourcePatternResolver) {
        this.resourcePatternResolver = resourcePatternResolver;
    }

    @Bean
    public OpenAPI customOpenAPI() {
        try {
            // 1. openapi-global.yml 파일을 명시적으로 로드하여 베이스로 설정
            Resource globalResource = resourcePatternResolver.getResource("classpath:openapi-global.yml");
            if (!globalResource.exists()) {
                throw new FileNotFoundException("openapi-global.yml 파일을 찾을 수 없습니다.");
            }

            OpenAPI mergedOpenAPI = parseYamlToOpenAPI(globalResource);

            // 2. 나머지 openapi-*.yml 파일을 검색 (global 파일 제외)
            Resource[] moduleResources = resourcePatternResolver.getResources("classpath*:openapi-*.yml");

            ParseOptions options = new ParseOptions();
            options.setResolve(true);

            // 3. 찾은 파일을 순회하며 병합합니다.
            for (Resource resource : moduleResources) {
                // Global 파일은 이미 로드했으므로 건너뜁니다.
                if (resource.getFilename().equals(globalResource.getFilename())) {
                    continue;
                }

                OpenAPI currentOpenAPI = parseYamlToOpenAPI(resource);

                if (currentOpenAPI != null) {
                    mergeOpenAPISpecs(mergedOpenAPI, currentOpenAPI);
                }
            }

            return mergedOpenAPI;

        } catch (IOException e) {
            throw new RuntimeException("OpenAPI YAML 파일 로드 중 오류 발생", e);
        }
    }

    // YAML 파일을 OpenAPI 객체로 파싱하는 유틸리티 메서드 추가
    private OpenAPI parseYamlToOpenAPI(Resource resource) throws IOException {
        String yamlContent = loadResourceContent(resource);
        ParseOptions options = new ParseOptions();
        options.setResolve(true);
        OpenAPI currentOpenAPI = new OpenAPIV3Parser().readContents(yamlContent, null, options).getOpenAPI();

        if (currentOpenAPI == null) {
            System.err.println("경고: " + resource.getFilename() + " 파일 파싱 실패 또는 내용 누락.");
        }
        return currentOpenAPI;
    }

    // 파일 내용을 문자열로 읽는 유틸리티 메서드
    private String loadResourceContent(Resource resource) throws IOException {
        try (Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
            return FileCopyUtils.copyToString(reader);
        }
    }

    // OpenAPI 객체의 Paths와 Components를 병합하는 핵심 로직
    private void mergeOpenAPISpecs(OpenAPI target, OpenAPI source) {

        // --- 1. Paths 병합 ---
        // (기존 로직 유지)
        if (source.getPaths() != null) {
            if (target.getPaths() == null) {
                target.setPaths(new Paths());
            }
            // Paths는 Key(URL) 기반으로 병합
            source.getPaths().forEach((path, pathItem) -> {
                // 중복 경로에 대한 처리 로직 추가 가능
                target.getPaths().addPathItem(path, pathItem);
            });
        }

        // --- 2. Tags 병합 (정보 병합) ---
        if (source.getTags() != null) {
            if (target.getTags() == null) {
                target.setTags(new java.util.ArrayList<>());
            }
            // List에 추가
            target.getTags().addAll(source.getTags());
        }

        // --- 3. Components 병합 (핵심) ---
        if (source.getComponents() != null) {
            if (target.getComponents() == null) {
                target.setComponents(new Components());
            }
            Components targetComponents = target.getComponents();
            Components sourceComponents = source.getComponents();

            // Schemas 병합
            if (sourceComponents.getSchemas() != null) {
                if (targetComponents.getSchemas() == null) targetComponents.setSchemas(new java.util.HashMap<>());
                targetComponents.getSchemas().putAll(sourceComponents.getSchemas());
            }

            // SecuritySchemes 병합 (인증/인가 설정)
            if (sourceComponents.getSecuritySchemes() != null) {
                if (targetComponents.getSecuritySchemes() == null) targetComponents.setSecuritySchemes(new java.util.HashMap<>());
                targetComponents.getSecuritySchemes().putAll(sourceComponents.getSecuritySchemes());
            }

            // Responses 병합
            if (sourceComponents.getResponses() != null) {
                if (targetComponents.getResponses() == null) targetComponents.setResponses(new java.util.HashMap<>());
                targetComponents.getResponses().putAll(sourceComponents.getResponses());
            }

            // Parameters 병합
            if (sourceComponents.getParameters() != null) {
                if (targetComponents.getParameters() == null) targetComponents.setParameters(new java.util.HashMap<>());
                targetComponents.getParameters().putAll(sourceComponents.getParameters());
            }

            // Headers, Examples, RequestBodies 등 나머지 Components 요소도 필요하면 추가 병합해야 합니다.
        }

        // --- 4. 기타 Top-Level 속성 병합 ---
        // 예: Security, Servers 등을 병합하거나 Global 파일의 것을 유지하도록 선택할 수 있습니다.
        // 여기서는 Global 파일의 설정을 유지하는 것을 권장합니다.
    }
}
