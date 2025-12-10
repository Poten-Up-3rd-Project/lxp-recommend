package com.lxp.auth.infrastructure.security;

import com.lxp.auth.domain.local.model.vo.HashedPassword;
import com.lxp.auth.domain.local.policy.PasswordPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DefaultPasswordPolicy implements PasswordPolicy {

    private final PasswordEncoder passwordEncoder;

    public DefaultPasswordPolicy(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public HashedPassword apply(String rawPassword) {
        return new HashedPassword(passwordEncoder.encode(rawPassword));
    }

    @Override
    public boolean isMatch(String rawPassword, HashedPassword hashedPassword) {
        return passwordEncoder.matches(rawPassword, hashedPassword.value());
    }
}
