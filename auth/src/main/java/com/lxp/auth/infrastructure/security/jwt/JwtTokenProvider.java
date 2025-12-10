package com.lxp.auth.infrastructure.security.jwt;

import com.lxp.auth.application.local.port.required.dto.AuthTokenInfo;
import com.lxp.auth.domain.common.exception.InvalidTokenException;
import com.lxp.auth.domain.common.exception.JwtAuthException;
import com.lxp.auth.domain.common.model.vo.TokenClaims;
import com.lxp.auth.domain.common.policy.JwtPolicy;
import com.lxp.auth.infrastructure.security.jwt.config.JwtConfig;
import com.lxp.auth.infrastructure.security.model.CustomUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider implements JwtPolicy {

    private final SecretKey key;
    private final long accessTokenValidityInMilliseconds;

    public JwtTokenProvider(JwtConfig jwtConfig) {
        this.key = jwtConfig.jwtSecretKey();
        this.accessTokenValidityInMilliseconds = jwtConfig.getAccessTokenValiditySeconds();
    }

    @Override
    public AuthTokenInfo createToken(TokenClaims claims) {
        long now = (new Date()).getTime();
        long expirationMillis = now + accessTokenValidityInMilliseconds;
        Date validity = new Date(expirationMillis);

        long expiresInSeconds = accessTokenValidityInMilliseconds / 1000;

        String authorities = String.join(",", claims.authorities());

        String token = Jwts.builder()
            .subject(claims.email())
            .claim("userId", claims.userId())
            .claim("auth", authorities)
            .expiration(validity)
            .signWith(key)
            .compact();
        return new AuthTokenInfo(token, expiresInSeconds);
    }

    @Override
    public Authentication getAuthentication(String token) {
        Claims claims = parseClaims(token);

        if (Objects.isNull(claims.get("auth"))) {
            throw new JwtAuthException("권한 정보가 없는 토큰입니다.");
        }

        Collection<? extends GrantedAuthority> authorities =
            Arrays.stream(claims.get("auth").toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        CustomUserDetails principal = new CustomUserDetails(
            claims.get("userId").toString(),
            claims.getSubject(),
            claims.getSubject(),
            "",
            authorities
        );

        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    @Override
    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Signature", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.", e);
        }
        return false;
    }

    @Override
    public long getExpirationTimeMillis(String token) {
        return parseClaims(token).getExpiration().getTime();
    }

    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(accessToken)
                .getPayload();
        } catch (ExpiredJwtException e) {
            return e.getClaims(); // 만료 시간 계산을 위한 Claims 반환
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            // 서명 오류, 토큰 형식 오류 등은 예외 처리
            throw new InvalidTokenException("Invalid JWT signature or format.");
        } catch (Exception e) {
            // 기타 모든 파싱 오류 (IllegalArgumentException 등)
            throw new InvalidTokenException("Invalid or unsupported JWT.");
        }
    }
}
