package com.lxp.recommend.infrastructure.web.internal.client.interceptor;

import com.lxp.recommend.infrastructure.constants.PassportConstants;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Feign 요청 시 Gateway로부터 받은 Passport를 자동으로 전달하는 인터셉터
 *
 * 동작 방식:
 * 1. SecurityContext에서 현재 인증 정보 조회
 * 2. Credentials에 저장된 Passport JWT 추출
 * 3. X-Passport 헤더에 추가하여 다른 서비스로 전달
 */
@Slf4j
@Component
public class FeignHeaderForwardInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        // 현재 요청 컨텍스트 확인
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attrs == null) {
            log.debug("No request context found, skipping Passport forwarding");
            return;
        }

        // SecurityContext에서 인증 정보 조회
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.getCredentials() != null) {
            // PassportAuthenticationFilter에서 저장한 Passport JWT 추출
            String passportJwt = String.valueOf(auth.getCredentials());

            log.debug("Forwarding Passport header in Feign request to: {}",
                    requestTemplate.url());

            // X-Passport 헤더 추가
            requestTemplate.header(PassportConstants.PASSPORT_HEADER_NAME, passportJwt);
        } else {
            log.warn("No authentication found in SecurityContext, Passport not forwarded");
        }
    }
}
