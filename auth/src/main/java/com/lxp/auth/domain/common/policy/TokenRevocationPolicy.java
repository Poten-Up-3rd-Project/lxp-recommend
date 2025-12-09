package com.lxp.auth.domain.common.policy;

public interface TokenRevocationPolicy {

    /**
     * 유효한 Access Token을 블랙리스트에 추가합니다.
     *
     * @param token           Access Token 문자열
     * @param durationSeconds 토큰의 남은 유효 시간 (TTL)
     */
    void revokeAccessToken(String token, long durationSeconds);

    /**
     * 해당 토큰이 블랙리스트에 있는지 확인합니다.
     *
     * @param token Access Token 문자열
     * @return 블랙리스트에 있다면 true
     */
    boolean isTokenBlacklisted(String token);
}
