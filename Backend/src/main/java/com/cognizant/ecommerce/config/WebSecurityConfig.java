package com.cognizant.ecommerce.config;

import com.cognizant.ecommerce.exception.CustomAuthenticationEntryPoint;
import com.cognizant.ecommerce.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import com.cognizant.ecommerce.exception.JwtAuthenticationException;
import com.cognizant.ecommerce.exception.JwtAccessDeniedException;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthFilter jwtAuthFilter;
    private final JwtAccessDeniedException jwtAccessDeniedException;
    private final JwtAuthenticationException jwtAuthenticationException;

    @Autowired
    private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        //public access
                                .requestMatchers(
                                        // User management
                                        "/api/admin/**",

                                        // Product management
                                        "/api/products/admin/**",

                                        // Payment management
                                        "/api/payments/admin/**",

                                        // Order management
                                        "/api/orders/admin/**",

                                        // Order item management
                                        "/api/order-items/admin/**",

                                        // Category management
                                        "/api/categories/admin/**",

                                        // Address management (admin view)
                                        "/api/addresses/admin/**",

                                        // Analytics
                                        "/api/analytics-reports/admin/**",

                                        "/api/admin/**"
                                ).hasRole("ADMIN")
                                .requestMatchers(
                                        // User profile
                                        "/api/user/**",

                                        // Payment methods
                                        "/api/payment-methods/**",

                                        // Payments (non-admin)
                                        "/api/payments",
                                        "/api/payments/*",
                                        "/api/payments/order/*",

                                        // Order items (view only)
                                        "/api/order-items/order/**",

                                        // Cart items
                                        "/api/cart-items/**",

                                        // Carts
                                        "/api/carts/**",

                                        // Orders (non-admin)
                                        "/api/orders",
                                        "/api/orders/*",
                                        "/api/orders/user/**",

                                        // Addresses (non-admin)
                                        "/api/addresses/**",

                                        "/api/me"



                                ).hasAnyRole("USER", "ADMIN")
                                // Public
                                .requestMatchers(
                                        "/api/auth/register",
                                        "/api/auth/login",
                                        "/api/auth/forgot-password",
                                        "/api/auth/reset-password",
                                        "/api/products",
                                        "/api/products/*",
                                        "/api/products/category/*",
                                        "/api/categories",
                                        "/api/categories/*",
                                        "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html"
                                ).permitAll()

                                .anyRequest().authenticated()

                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(jwtAuthenticationException) // 401 handler
                        .accessDeniedHandler(jwtAccessDeniedException) // 403 handler
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder();
    }
}

