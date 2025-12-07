package com.alertix;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Alertix Monitoring - Main Application Entry Point
 * 
 * This is the main Spring Boot application class for the Alertix Monitoring
 * platform.
 * It enables:
 * - Auto-configuration for all Spring Boot features
 * - Component scanning for all @Component, @Service, @Repository, @Controller
 * classes
 * - Async task execution
 * - Scheduled task execution
 * 
 * @author Alertix Team
 * @version 1.0
 * @since 2025-12-07
 */
@SpringBootApplication
@EnableAsync
@EnableScheduling
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
