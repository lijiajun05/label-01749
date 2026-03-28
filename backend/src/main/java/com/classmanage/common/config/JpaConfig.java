package com.classmanage.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * JPA 配置
 */
@Configuration
@EnableJpaAuditing
@EnableTransactionManagement
public class JpaConfig {
}
