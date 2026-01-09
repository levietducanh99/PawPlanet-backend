package com.pawpplanet.backend.explore.controller;

import com.pawpplanet.backend.explore.dto.ExploreResponse;
import com.pawpplanet.backend.explore.service.ExploreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/explore")
@RequiredArgsConstructor
@Tag(name = "Explore", description = "APIs for discovering random posts, pets, and users")
public class ExploreController {

    private final ExploreService exploreService;

    @GetMapping
    @Operation(
            summary = "Get explore feed",
            description = "Returns a randomized mix of public posts, pets, and users for discovery. " +
                    "Authentication is optional - logged-in users won't see their own content. " +
                    "Use the returned seed for consistent pagination."
    )
    public ResponseEntity<ExploreResponse> getExploreFeed(
            @Parameter(description = "Total number of items to return (default: 30, max: 50)")
            @RequestParam(required = false) Integer limit,

            @Parameter(description = "Seed for consistent random ordering (for pagination)")
            @RequestParam(required = false) String seed,

            @Parameter(description = "Comma-separated list of entity types to include: post,pet,user (default: all)")
            @RequestParam(required = false) String include
    ) {
        return ResponseEntity.ok(exploreService.getExploreFeed(limit, seed, include));
    }
}

