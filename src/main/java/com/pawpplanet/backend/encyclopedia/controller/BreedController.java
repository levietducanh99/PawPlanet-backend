package com.pawpplanet.backend.encyclopedia.controller;

import com.pawpplanet.backend.common.dto.ApiResponse;
import com.pawpplanet.backend.common.dto.PagedResult;
import com.pawpplanet.backend.encyclopedia.dto.BreedResponse;
import com.pawpplanet.backend.encyclopedia.service.BreedService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/encyclopedia/breeds")
@RequiredArgsConstructor
@Tag(name = "Encyclopedia - Breeds", description = "API quản lý các giống (breeds)")
public class BreedController {

    private final BreedService service;

    @GetMapping
    @Operation(summary = "Lấy danh sách breeds có phân trang, optional filter: speciesId, taxonomyType")
    public ResponseEntity<ApiResponse<PagedResult<BreedResponse>>> list(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "speciesId", required = false) Long speciesId,
            @RequestParam(value = "taxonomyType", required = false) String taxonomyType
    ) {
        ApiResponse<PagedResult<BreedResponse>> resp = new ApiResponse<>();
        resp.setResult(service.getBreeds(page, size, speciesId, taxonomyType));
        resp.setStatusCode(0);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/all")
    @Operation(summary = "Lấy danh sách breeds theo species (có phân trang). Tham số: speciesId, page, size")
    public ResponseEntity<ApiResponse<PagedResult<BreedResponse>>> listAllBySpecies(
            @RequestParam("speciesId") Long speciesId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        ApiResponse<PagedResult<BreedResponse>> resp = new ApiResponse<>();
        resp.setResult(service.getBreeds(page, size, speciesId, null));
        resp.setStatusCode(0);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy chi tiết breed theo ID")
    public ResponseEntity<ApiResponse<BreedResponse>> getById(@PathVariable Long id) {
        ApiResponse<BreedResponse> resp = new ApiResponse<>();
        service.getById(id).ifPresent(resp::setResult);
        resp.setStatusCode(0);
        return ResponseEntity.ok(resp);
    }
}
