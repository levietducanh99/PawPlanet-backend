package com.pawpplanet.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

/**
 * Simple health check controller for quick backend testing.
 */
@RestController
@RequestMapping("/api/v1")
@Tag(name = "Health", description = "API kiểm tra trạng thái server")
public class HealthController {

    @GetMapping("/health")
    @Operation(
            summary = "Health check",
            description = "Kiểm tra backend có hoạt động không"
    )
    public Map<String, Object> health() {
        return Map.of(
                "status", "UP",
                "timestamp", Instant.now().toString()
        );
    }
}
