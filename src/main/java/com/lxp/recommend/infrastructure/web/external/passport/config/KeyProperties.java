package com.lxp.recommend.infrastructure.web.external.passport.config;

import io.jsonwebtoken.security.Keys;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

/**
 * Passport JWT 검증을 위한 Secret Key 설정
 */
@Setter
@Configuration
@ConfigurationProperties(prefix = "passport.key")
@Profile("!(test | persistence)")
public class KeyProperties {

    private String secretKey;

    @Bean
    public SecretKey passportSecretKey() {
        if (secretKey == null || secretKey.isBlank()) {
            throw new IllegalStateException("passport.key.secret-key cannot be null or empty");
        }
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }
}
