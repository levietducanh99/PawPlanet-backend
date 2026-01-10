package com.pawpplanet.backend.search.controller;

import com.pawpplanet.backend.search.dto.GlobalSearchResponse;
import com.pawpplanet.backend.search.service.GlobalSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
@Tag(name = "Global Search", description = "Search across users and pets")
public class GlobalSearchController {

    private final GlobalSearchService globalSearchService;

    @GetMapping
    @Operation(
        summary = "Global search for users and pets",
        description = "Search for users by username and pets by name or breed. " +
                      "Keyword must be at least 2 characters. " +
                      "Use 'types' parameter to filter results (user, pet, or both)."
    )
    public ResponseEntity<GlobalSearchResponse> search(
            @Parameter(description = "Search keyword (minimum 2 characters)", required = true)
            @RequestParam("q") String keyword,
            
            @Parameter(description = "Entity types to search (comma-separated: user, pet). If not specified, searches all types.")
            @RequestParam(value = "types", required = false) String types,
            
            @Parameter(description = "Maximum number of results per type (default: 10)")
            @RequestParam(value = "limit", required = false) Integer limit
    ) {
        GlobalSearchResponse response = globalSearchService.search(keyword, types, limit);
        return ResponseEntity.ok(response);
    }
}
