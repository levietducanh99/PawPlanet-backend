package com.pawpplanet.backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for Swagger/OpenAPI documentation.
 * Provides API documentation accessible at /swagger-ui.html
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("PawPlanet Backend API")
                        .version("1.0.0")
                        .description("REST API documentation for PawPlanet Backend application. " +
                                "This API provides endpoints for managing pet-related services.")
                        .contact(new Contact()
                                .name("PawPlanet Team")
                                .email("support@pawpplanet.com")
                                .url("https://github.com/levietducanh99/PawpPanet-backend"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")));
    }
}
