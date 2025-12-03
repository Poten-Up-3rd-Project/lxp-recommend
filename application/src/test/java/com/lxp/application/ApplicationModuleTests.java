package com.lxp.application;

import com.lxp.auth.AuthConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

@SpringBootTest
class ApplicationModuleTests {

    /**
     * 프로젝트의 모듈 구조가 Modulith 규칙을 준수하는지 검증합니다.
     */
    @Test
    void verifyModularity() {
        // ApplicationModules.of(Class<?>): Modulith가 컨텍스트에서 모듈을 찾기 시작하는 시작점을 지정합니다.
        // 여기서는 auth 모듈의 설정 클래스를 사용하지만, 어떤 도메인 모듈의 설정 클래스를 사용해도 무방합니다.
        ApplicationModules.of(MainApplication.class).verify();
    }

    /**
     * (선택 사항) 모듈 간의 의존성 구조를 시각화하는 문서를 생성합니다.
     */
    @Test
    void createModuleDocumentation() {
        // 문서를 생성할 시작점을 지정합니다.
        ApplicationModules modules = ApplicationModules.of(MainApplication.class);

        // AsciiDoc 형식의 문서를 'build/generated-docs/modularity' 경로에 생성합니다.
        // 이 문서는 모듈 경계, 매핑, 모듈 간 의존성 다이어그램을 포함합니다.
        new Documenter(modules)
                .writeDocumentation();
    }
}