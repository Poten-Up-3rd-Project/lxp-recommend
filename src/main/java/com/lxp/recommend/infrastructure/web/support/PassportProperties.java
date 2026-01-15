package com.lxp.recommend.infrastructure.web.support;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * Passport JWT 검증을 위한 설정
 * application.yml의 passport.* 속성을 바인딩
 */
@Component
@ConfigurationProperties(prefix = "passport")
@Getter
@Profile("!(test | persistence)")
public class PassportProperties {

    private String publicKey;
    private String issuer = "lxp-gateway";

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    /**
     * Base64 인코딩된 Public Key 문자열을 PublicKey 객체로 변환
     */
    public PublicKey getPublicKeyObject() {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(publicKey);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePublic(spec);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load public key", e);
        }
    }
}
