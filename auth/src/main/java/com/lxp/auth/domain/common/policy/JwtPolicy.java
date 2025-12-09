package com.lxp.auth.domain.common.policy;

import com.lxp.auth.application.local.port.required.dto.AuthTokenInfo;
import com.lxp.auth.domain.common.model.vo.TokenClaims;

public interface JwtPolicy {

    /**
     * 유저 정보를 사용하여 Access Token을 생성합니다.
     *
     * @param claims
     * @return JWT Token
     */
    AuthTokenInfo createToken(TokenClaims claims);

    /**
     * JWT 토큰을 복호화하여 인증 정보를 추출합니다.
     *
     * @param token 복호화할 JWT 문자열
     * @return Spring Security Authentication 객체
     */
    Object getAuthentication(String token);

    /**
     * 토큰의 유효성을 검증합니다.
     *
     * @param token 검증할 JWT 문자열
     * @return 유효성 여부
     */
    boolean validateToken(String token);

    /**
     * JWT 토큰을 복호화하여 만료 시간(exp) 클레임을 추출합니다.
     *
     * @param token JWT 문자열
     * @return 만료 시간 (밀리초 단위의 Unix Timestamp)
     */
    long getExpirationTimeMillis(String token);
}
