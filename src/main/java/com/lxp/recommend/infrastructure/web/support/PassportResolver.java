package com.lxp.recommend.infrastructure.web.support;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.security.PublicKey;

/**
 * X-Passport 헤더에서 JWT를 추출하고 검증하여 Passport 객체로 변환
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Profile("!(test | persistence)")
public class PassportResolver {

    private static final String PASSPORT_HEADER = "X-Passport";

    private final PassportProperties properties;

    /**
     * HttpServletRequest에서 X-Passport 헤더를 추출하여 검증 후 Passport 반환
     *
     * @param request HTTP 요청
     * @return Passport 객체
     * @throws IllegalArgumentException X-Passport 헤더가 없거나 유효하지 않은 경우
     */
    public Passport resolve(HttpServletRequest request) {
        String token = request.getHeader(PASSPORT_HEADER);

        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("X-Passport header is missing");
        }

        return parseToken(token);
    }

    /**
     * JWT 토큰을 파싱하여 Passport 객체 생성
     */
    private Passport parseToken(String token) {
        try {
            PublicKey publicKey = properties.getPublicKeyObject();

            Claims claims = Jwts.parser()
                    .verifyWith(publicKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String userId = claims.get("uid", String.class);
            String role = claims.get("rol", String.class);
            String traceId = claims.get("tid", String.class);

            return new Passport(userId, role, traceId);

        } catch (Exception e) {
            log.error("Failed to parse passport token", e);
            throw new IllegalArgumentException("Invalid passport token", e);
        }
    }
}

/**
 * 기능:
 *
 * X-Passport 헤더 추출
 *
 * RSA Public Key로 JWT 서명 검증
 *
 * Claims에서 uid, rol, tid 추출
 *
 * 검증 실패 시 예외 발생
 */