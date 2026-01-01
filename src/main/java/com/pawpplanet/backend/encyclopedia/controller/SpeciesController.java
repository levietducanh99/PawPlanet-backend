package com.pawpplanet.backend.encyclopedia.controller;

import com.pawpplanet.backend.common.dto.ApiResponse;
import com.pawpplanet.backend.common.dto.PagedResult;
import com.pawpplanet.backend.encyclopedia.dto.SpeciesResponse;
import com.pawpplanet.backend.encyclopedia.service.SpeciesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/v1/encyclopedia/species")
@RequiredArgsConstructor
@Tag(name = "Encyclopedia - Species", description = "API quản lý các loài (species)")
public class SpeciesController {

    private final SpeciesService service;

    @GetMapping
    @Operation(summary = "Lấy danh sách species có phân trang")
    public ResponseEntity<ApiResponse<PagedResult<SpeciesResponse>>> list(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "classId", required = false) Long classId
    ) {
        ApiResponse<PagedResult<SpeciesResponse>> resp = new ApiResponse<>();
        resp.setResult(service.getSpecies(page, size, classId));
        resp.setStatusCode(0);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy thông tin species theo ID")
    public ResponseEntity<ApiResponse<SpeciesResponse>> getById(@PathVariable Long id) {
        ApiResponse<SpeciesResponse> resp = new ApiResponse<>();
        service.getById(id).ifPresent(resp::setResult);
        resp.setStatusCode(0);
        return ResponseEntity.ok(resp);
    }
}

