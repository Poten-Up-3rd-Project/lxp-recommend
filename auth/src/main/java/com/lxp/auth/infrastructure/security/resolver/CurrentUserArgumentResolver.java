package com.lxp.auth.infrastructure.security.resolver;

import com.lxp.auth.infrastructure.security.model.CustomUserDetails;
import com.lxp.common.annotation.CurrentUserId;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Objects;

@Slf4j
@Component
public class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentUserId.class) &&
            parameter.getParameterType().equals(String.class);
    }

    @Override
    public @Nullable String resolveArgument(MethodParameter parameter,
                                            @Nullable ModelAndViewContainer mavContainer,
                                            NativeWebRequest webRequest,
                                            @Nullable WebDataBinderFactory binderFactory) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (Objects.isNull(authentication) || !authentication.isAuthenticated()) {
            log.debug("Argument resolution skipped: Authentication object is null or user is not authenticated.");
            return null;
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof String longPrincipal) {
            return longPrincipal;
        } else if (principal instanceof CustomUserDetails c) {
            return c.getUserId();
        }

        log.info("Argument resolution failed: Principal type is not supported. Type: {}", principal.getClass().getName());
        return null;
    }
}
