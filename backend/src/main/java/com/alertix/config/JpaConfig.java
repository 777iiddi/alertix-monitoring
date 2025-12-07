package com.alertix.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * JPA Configuration
 * 
 * Enables JPA auditing for automatic createdAt/updatedAt timestamp management
 */
@Configuration
@EnableJpaAuditing
public class JpaConfig {
    // JPA Auditing is now enabled
    // BaseEntity @CreatedDate and @LastModifiedDate will work automatically
}
