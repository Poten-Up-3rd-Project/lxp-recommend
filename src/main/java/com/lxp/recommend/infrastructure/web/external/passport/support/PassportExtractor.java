package com.lxp.recommend.infrastructure.web.external.passport.support;

import com.lxp.recommend.infrastructure.constants.PassportConstants;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

/**
 * HTTP 요청에서 X-Passport 헤더를 추출
 */
@Component
public class PassportExtractor {

    public String extract(HttpServletRequest request) {
        return request.getHeader(PassportConstants.PASSPORT_HEADER_NAME);
    }
}
