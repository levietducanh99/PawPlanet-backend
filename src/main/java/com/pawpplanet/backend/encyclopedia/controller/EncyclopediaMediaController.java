package com.pawpplanet.backend.encyclopedia.controller;

import com.pawpplanet.backend.common.dto.ApiResponse;
import com.pawpplanet.backend.common.dto.PagedResult;
import com.pawpplanet.backend.encyclopedia.dto.AddEncyclopediaMediaRequest;
import com.pawpplanet.backend.encyclopedia.dto.AddEncyclopediaMediaResponse;
import com.pawpplanet.backend.encyclopedia.dto.EncyclopediaMediaResponse;
import com.pawpplanet.backend.encyclopedia.service.EncyclopediaMediaService;
import com.pawpplanet.backend.utils.SecurityHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/encyclopedia")
@RequiredArgsConstructor
@Tag(name = "Encyclopedia - Media", description = "API quản lý media của encyclopedia")
public class EncyclopediaMediaController {

    private final EncyclopediaMediaService mediaService;
    private final SecurityHelper securityHelper;

    @GetMapping("/species/{speciesId}/gallery")
    @Operation(summary = "Lấy gallery của species với phân trang")
    public ResponseEntity<ApiResponse<PagedResult<EncyclopediaMediaResponse>>> getSpeciesGallery(
            @PathVariable Long speciesId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "20") int size
    ) {
        ApiResponse<PagedResult<EncyclopediaMediaResponse>> resp = new ApiResponse<>();
        resp.setResult(mediaService.getGallery("species", speciesId, page, size));
        resp.setStatusCode(0);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/breeds/{breedId}/gallery")
    @Operation(summary = "Lấy gallery của breed với phân trang")
    public ResponseEntity<ApiResponse<PagedResult<EncyclopediaMediaResponse>>> getBreedGallery(
            @PathVariable Long breedId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "20") int size
    ) {
        ApiResponse<PagedResult<EncyclopediaMediaResponse>> resp = new ApiResponse<>();
        resp.setResult(mediaService.getGallery("breed", breedId, page, size));
        resp.setStatusCode(0);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/classes/{classId}/gallery")
    @Operation(summary = "Lấy gallery của class với phân trang")
    public ResponseEntity<ApiResponse<PagedResult<EncyclopediaMediaResponse>>> getClassGallery(
            @PathVariable Long classId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "20") int size
    ) {
        ApiResponse<PagedResult<EncyclopediaMediaResponse>> resp = new ApiResponse<>();
        resp.setResult(mediaService.getGallery("class", classId, page, size));
        resp.setStatusCode(0);
        return ResponseEntity.ok(resp);
    }

    // ==================== ADMIN ENDPOINTS ====================

    @PostMapping("/species/{speciesId}/media")
    @Operation(
            summary = "[ADMIN] Thêm media vào species",
            description = "Chỉ admin mới có thể thêm ảnh/video vào encyclopedia. Có thể thêm hero, gallery, hoặc thumbnail.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<ApiResponse<AddEncyclopediaMediaResponse>> addMediaToSpecies(
            @PathVariable Long speciesId,
            @RequestBody @Valid AddEncyclopediaMediaRequest request
    ) {
        // Verify admin access
        securityHelper.requireAdmin();

        ApiResponse<AddEncyclopediaMediaResponse> resp = new ApiResponse<>();
        resp.setResult(mediaService.addMediaToSpecies(speciesId, request));
        resp.setStatusCode(0);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/breeds/{breedId}/media")
    @Operation(
            summary = "[ADMIN] Thêm media vào breed",
            description = "Chỉ admin mới có thể thêm ảnh/video vào encyclopedia. Có thể thêm hero, gallery, hoặc thumbnail.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<ApiResponse<AddEncyclopediaMediaResponse>> addMediaToBreed(
            @PathVariable Long breedId,
            @RequestBody @Valid AddEncyclopediaMediaRequest request
    ) {
        // Verify admin access
        securityHelper.requireAdmin();

        ApiResponse<AddEncyclopediaMediaResponse> resp = new ApiResponse<>();
        resp.setResult(mediaService.addMediaToBreed(breedId, request));
        resp.setStatusCode(0);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/classes/{classId}/media")
    @Operation(
            summary = "[ADMIN] Thêm media vào animal class",
            description = "Chỉ admin mới có thể thêm ảnh/video vào encyclopedia. Có thể thêm hero, gallery, hoặc thumbnail.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<ApiResponse<AddEncyclopediaMediaResponse>> addMediaToClass(
            @PathVariable Long classId,
            @RequestBody @Valid AddEncyclopediaMediaRequest request
    ) {
        // Verify admin access
        securityHelper.requireAdmin();

        ApiResponse<AddEncyclopediaMediaResponse> resp = new ApiResponse<>();
        resp.setResult(mediaService.addMediaToClass(classId, request));
        resp.setStatusCode(0);
        return ResponseEntity.ok(resp);
    }
}

