package com.lxp.content;

import com.lxp.common.infrastructure.persistence.JpaAuditingConfig;
import com.lxp.common.infrastructure.retry.DefaultRetryPolicy;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.modulith.ApplicationModule;

@Configuration
@ApplicationModule
@Import({DefaultRetryPolicy.class, JpaAuditingConfig.class})
public class ContentConfiguration {
}
