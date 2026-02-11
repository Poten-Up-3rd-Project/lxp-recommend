package com.lxp.recommend.global.config;

import com.lxp.passport.bean.filter.PassportFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration
@RequiredArgsConstructor
public class PassportConfig {

    private final PassportFilter passportFilter;

    @Bean
    public FilterRegistrationBean<PassportFilter> passportFilterRegistration() {
        FilterRegistrationBean<PassportFilter> bean = new FilterRegistrationBean<>(passportFilter);

        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        bean.addUrlPatterns("/*");
        return bean;
    }

}
