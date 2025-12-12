package com.lxp.content.config;

import com.lxp.common.application.cqrs.CommandBus;
import com.lxp.common.infrastructure.cqrs.SimpleCommandBus;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CqrsConfig {
    @Bean
    public CommandBus commandBus(ApplicationContext applicationContext) {
        return new SimpleCommandBus(applicationContext);
    }
}
