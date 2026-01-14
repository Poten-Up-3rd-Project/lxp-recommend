package com.lxp.recommend.infrastructure.web.external.passport.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * 인증 실패 시 401 Unauthorized 응답 처리
 */
@Component
@RequiredArgsConstructor
public class PassportAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException)
            throws IOException, ServletException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        Map<String, Object> errorResponse = Map.of(
                "success", false,
                "error", Map.of(
                        "code", "UNAUTHORIZED_ACCESS",
                        "message", "인증되지 않은 접근입니다",
                        "field", "GLOBAL"
                )
        );

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
