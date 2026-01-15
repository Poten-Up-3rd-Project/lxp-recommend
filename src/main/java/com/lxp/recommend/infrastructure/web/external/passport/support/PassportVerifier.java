package com.lxp.recommend.infrastructure.web.external.passport.support;

import com.lxp.recommend.infrastructure.constants.PassportConstants;
import com.lxp.recommend.infrastructure.web.external.passport.config.KeyProperties;
import com.lxp.recommend.infrastructure.web.external.passport.exception.InvalidPassportException;
import com.lxp.recommend.infrastructure.web.external.passport.model.PassportClaims;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Arrays;
import java.util.List;

/**
 * Passport JWT 검증 및 클레임 추출
 */
@Component
@Profile("!(test | persistence)")
public class PassportVerifier {

    private final SecretKey key;

    public PassportVerifier(KeyProperties keyProperties) {
        this.key = keyProperties.passportSecretKey();
    }

    /**
     * Passport JWT를 검증하고 클레임을 추출합니다.
     *
     * @param encodedPassport Passport JWT 문자열
     * @return PassportClaims 객체
     * @throws InvalidPassportException JWT 검증 실패 시
     */
    public PassportClaims verify(String encodedPassport) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(encodedPassport)
                    .getPayload();

            String userId = claims.get(PassportConstants.PASSPORT_USER_ID, String.class);
            String rolesString = claims.get(PassportConstants.PASSPORT_ROLE, String.class);
            String traceId = claims.get(PassportConstants.PASSPORT_TRACE_ID, String.class);

            List<String> roles = rolesString != null
                    ? Arrays.asList(rolesString.split(","))
                    : List.of();

            return new PassportClaims(userId, roles, traceId);

        } catch (ExpiredJwtException e) {
            throw new InvalidPassportException("Expired passport", e);

        } catch (JwtException | IllegalArgumentException e) {
            throw new InvalidPassportException("Invalid passport", e);
        }
    }
}
