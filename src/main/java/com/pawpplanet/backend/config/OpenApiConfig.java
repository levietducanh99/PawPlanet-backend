package com.pawpplanet.backend.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "PawPlanet API",
                version = "v1",
                description = "PawPlanet Backend API - Social network for pets",
                contact = @Contact(
                        name = "PawPlanet Team",
                        email = "support@pawplanet.com"
                )
        )
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "JWT Authentication - Nhập token nhận được từ /api/v1/auth/login"
)
public class OpenApiConfig {
}

