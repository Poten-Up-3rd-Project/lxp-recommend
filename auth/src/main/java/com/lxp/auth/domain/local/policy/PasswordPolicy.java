package com.lxp.auth.domain.local.policy;

import com.lxp.auth.domain.local.model.vo.HashedPassword;

/**
 * 비밀번호의 정책을 결정합니다.
 */
public interface PasswordPolicy {

    /**
     * 비밀번호를 인코딩합니다.
     *
     * @param rawPassword
     * @return hashedPassword
     */
    HashedPassword apply(String rawPassword);

    /**
     * 비밀번호가 동일한지 확인합니다.
     *
     * @param rawPassword
     * @param hashedPassword
     * @return 동일할 경우 true, 아닐 경우 false
     */
    boolean isMatch(String rawPassword, HashedPassword hashedPassword);

}
