package com.pawpplanet.backend.auth.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Public endpoints that don't require authentication
    private final String[] PUBLIC_ENDPOINTS = {
            // Swagger UI & OpenAPI Documentation
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/api-docs/**",
            "/openapi.yaml",

            // Static resources (required for Swagger UI)
            "/webjars/**",
            "/favicon.ico",

            // Health check & Actuator
            "/health",
            "/actuator/**"
    };

    // Auth endpoints (login, register, etc.)
    private final String[] AUTH_ENDPOINTS = {
            "/api/v1/auth/**"
    };


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Allow all origins in development. For production, specify exact origins
        configuration.setAllowedOrigins(Arrays.asList("*"));

        // Allow all HTTP methods
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

        // Allow all headers
        configuration.setAllowedHeaders(Arrays.asList("*"));

        // Allow credentials (cookies, authorization headers)
        // Note: If using allowCredentials(true), you cannot use "*" for allowedOrigins
        // configuration.setAllowCredentials(true);

        // Expose headers to client
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Type"));

        // Cache preflight response for 1 hour
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, CustomJwtDecoder customJwtDecoder) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);

        // Enable CORS
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));

        http.authorizeHttpRequests(request ->
                request
                        // CORS preflight - MUST BE FIRST
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Swagger UI & OpenAPI
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/api-docs/**",
                                "/openapi.yaml",
                                "/webjars/**",
                                "/favicon.ico"
                        ).permitAll()

                        // Health check & Actuator
                        .requestMatchers("/health", "/api/v1/health", "/actuator/**").permitAll()

                        // Auth endpoints - login, register, etc.
                        .requestMatchers(HttpMethod.POST, AUTH_ENDPOINTS).permitAll()
                        .requestMatchers(HttpMethod.GET, AUTH_ENDPOINTS).permitAll()



                        // Public GET endpoints - read-only access
                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/posts/**",
                                "/api/v1/users/**",
                                "/api/v1/pets/**",
                                "/api/v1/encyclopedia/**").permitAll().anyRequest().authenticated());

        http.
                oauth2ResourceServer(
                        oauth2
                                -> oauth2.jwt(jwtConfigurer -> jwtConfigurer.decoder(customJwtDecoder)));

        return http.build();
    }


}