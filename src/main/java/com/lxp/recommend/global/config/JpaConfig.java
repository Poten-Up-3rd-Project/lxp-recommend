package com.lxp.recommend.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.lxp.recommend.adapter.out.persistence")
public class JpaConfig {
}
