package com.lxp.recommend.infrastructure.web.external.passport.model;

import java.util.List;

/**
 * Passport JWT에서 추출한 클레임 정보
 */
public record PassportClaims(
        String userId,
        List<String> roles,
        String traceId
) {
}
