package com.lxp.recommend.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;

@TestConfiguration
public class TestSecurityConfig {

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        // 모든 요청 인증 무시 (테스트용)
        return (web) -> web.ignoring()
                .requestMatchers("/**");
    }
}
