package com.lxp.recommend.infrastructure.web.support;

import com.lxp.recommend.infrastructure.web.external.passport.model.PassportClaims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * SecurityContext에서 Passport 정보 추출
 * PassportAuthenticationFilter가 이미 검증하고 저장한 정보를 재사용
 *
 * 장점:
 * - JWT 재파싱 없음 (성능 향상)
 * - 요청당 1번만 검증 (일관성 보장)
 * - Spring Security 표준 방식
 */
@Slf4j
@Component
@ConditionalOnProperty(
        prefix = "passport",
        name = "enabled",
        havingValue = "true"
)
public class PassportResolver {

    /**
     * SecurityContext에서 이미 검증된 Passport 정보 조회
     *
     * @param request HTTP 요청 (현재는 미사용, 인터페이스 호환성 유지)
     * @return PassportClaims 객체
     * @throws IllegalArgumentException 인증 정보가 없거나 유효하지 않은 경우
     */
    public PassportClaims resolve(HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            log.error("No authenticated user found in SecurityContext");
            throw new IllegalArgumentException("No authenticated user found");
        }

        // PassportAuthenticationFilter에서 저장한 정보 추출
        String userId = auth.getPrincipal().toString();

        List<String> roles = auth.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .toList();

        // traceId는 MDC에서 추출 (PassportAuthenticationFilter가 MDC에 저장)
        String traceId = org.slf4j.MDC.get("traceId");

        log.debug("Resolved passport from SecurityContext: userId={}, roles={}, traceId={}",
                userId, roles, traceId);

        return new PassportClaims(userId, roles, traceId);
    }
}
