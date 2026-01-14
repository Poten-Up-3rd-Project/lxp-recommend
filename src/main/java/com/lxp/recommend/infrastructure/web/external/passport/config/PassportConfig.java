package com.lxp.recommend.infrastructure.web.external.passport.config;

import com.lxp.recommend.infrastructure.web.external.passport.filter.PassportAuthenticationFilter;
import com.lxp.recommend.infrastructure.web.external.passport.filter.PassportAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 설정
 * Passport 인증 필터 적용
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class PassportConfig {

    private final PassportAuthenticationFilter passportAuthenticationFilter;
    private final PassportAuthenticationEntryPoint authenticationEntryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authz -> authz
                        // Health Check 및 Actuator는 인증 제외
                        .requestMatchers("/health", "/actuator/**").permitAll()
                        // Internal API는 인증 제외 (다른 서비스 간 호출)
                        .requestMatchers("/internal/**").permitAll()
                        // 나머지는 인증 필요
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationEntryPoint)
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // Passport 필터를 Spring Security 필터 체인에 추가
                .addFilterBefore(
                        passportAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}
