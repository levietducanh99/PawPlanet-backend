package com.pawpplanet.backend.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

/**
 * Load .env file EARLY in Spring Boot lifecycle (before DataSource initialization).
 * Converts Heroku-style DATABASE_URL (postgres://) to JDBC format.
 */
public class DotenvConfig implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        try {
            Dotenv dotenv = Dotenv.configure()
                    .ignoreIfMissing()
                    .load();

            Map<String, Object> properties = new HashMap<>();

            dotenv.entries().forEach(entry -> {
                String key = entry.getKey();
                String value = entry.getValue();

                // Convert Heroku DATABASE_URL format to JDBC format
                if ("DATABASE_URL".equals(key) && value.startsWith("postgres://")) {
                    try {
                        URI uri = new URI(value);
                        String host = uri.getHost();
                        int port = uri.getPort() > 0 ? uri.getPort() : 5432;
                        String database = uri.getPath().substring(1); // remove leading /

                        String jdbcUrl = String.format("jdbc:postgresql://%s:%d/%s", host, port, database);
                        properties.put("spring.datasource.url", jdbcUrl);

                        // Parse username and password from URL
                        String userInfo = uri.getUserInfo();
                        if (userInfo != null && userInfo.contains(":")) {
                            String[] credentials = userInfo.split(":", 2);
                            properties.put("spring.datasource.username", credentials[0]);
                            properties.put("spring.datasource.password", credentials[1]);
                        }

                        System.out.println("✅ Converted DATABASE_URL to JDBC: " + jdbcUrl);
                    } catch (URISyntaxException e) {
                        System.err.println("❌ Failed to parse DATABASE_URL: " + e.getMessage());
                    }
                } else {
                    properties.put(key, value);
                }
            });

            if (!properties.isEmpty()) {
                MapPropertySource propertySource = new MapPropertySource("dotenvProperties", properties);
                environment.getPropertySources().addFirst(propertySource);
                System.out.println("✅ Loaded " + properties.size() + " properties from .env");
            }
        } catch (Exception e) {
            System.err.println("⚠️ Warning: Could not load .env file: " + e.getMessage());
        }
    }
}
