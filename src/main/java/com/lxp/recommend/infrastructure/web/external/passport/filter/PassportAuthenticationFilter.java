package com.lxp.recommend.infrastructure.web.external.passport.filter;

import com.lxp.recommend.infrastructure.web.external.passport.model.PassportClaims;
import com.lxp.recommend.infrastructure.web.external.passport.support.PassportExtractor;
import com.lxp.recommend.infrastructure.web.external.passport.support.PassportVerifier;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * X-Passport 헤더에서 JWT를 추출하여 SecurityContext에 인증 정보 설정
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Profile("!(test | persistence)")
public class PassportAuthenticationFilter extends OncePerRequestFilter {

    private final PassportExtractor extractor;
    private final PassportVerifier verifier;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String encodedPassport = extractor.extract(request);

        if (encodedPassport != null) {
            try {
                PassportClaims claims = verifier.verify(encodedPassport);

                // UsernamePasswordAuthenticationToken 생성
                // principal: userId, credentials: Passport JWT, authorities: 권한 목록
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                claims.userId(),
                                encodedPassport,  // Credentials에 JWT 저장
                                claims.roles().stream()
                                        .map(SimpleGrantedAuthority::new)
                                        .toList()
                        );

                authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // SecurityContext에 인증 정보 설정
                if (SecurityContextHolder.getContext().getAuthentication() == null) {
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }

                // 분산 추적을 위해 MDC에 traceId 저장
                MDC.put("traceId", claims.traceId());

                log.debug("Passport authenticated: userId={}, traceId={}",
                        claims.userId(), claims.traceId());

            } catch (Exception e) {
                log.error("Passport authentication failed", e);
                throw new BadCredentialsException("Invalid passport", e);
            }
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }
}
