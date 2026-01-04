package com.pawpplanet.backend.encyclopedia.controller;

import com.pawpplanet.backend.common.dto.ApiResponse;
import com.pawpplanet.backend.encyclopedia.dto.SearchResponse;
import com.pawpplanet.backend.encyclopedia.service.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/encyclopedia/search")
@RequiredArgsConstructor
@Tag(name = "Encyclopedia - Search", description = "Search across classes, species and breeds")
public class SearchController {

    private final SearchService searchService;

    @GetMapping
    @Operation(summary = "Tìm kiếm theo từ khoá across classes/species/breeds")
    public ResponseEntity<ApiResponse<SearchResponse>> search(@RequestParam("q") String q) {
        ApiResponse<SearchResponse> resp = new ApiResponse<>();
        resp.setResult(searchService.searchAll(q));
        resp.setStatusCode(0);
        return ResponseEntity.ok(resp);
    }
}

