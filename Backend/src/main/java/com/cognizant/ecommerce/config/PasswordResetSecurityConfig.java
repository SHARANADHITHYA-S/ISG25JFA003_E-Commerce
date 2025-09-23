package com.cognizant.ecommerce.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class PasswordResetSecurityConfig {

    @Bean
    @Order(1)
    public SecurityFilterChain passwordResetFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .securityMatcher("/api/auth/password/**")
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/auth/password/forgot-password",
                                "/api/auth/password/reset-password"
                        ).permitAll()
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}