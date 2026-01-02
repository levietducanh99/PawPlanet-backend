package com.pawpplanet.backend.media.controller;

import com.pawpplanet.backend.media.dto.MediaSignRequest;
import com.pawpplanet.backend.media.dto.MediaSignResponse;
import com.pawpplanet.backend.media.service.MediaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/media")
@RequiredArgsConstructor
public class MediaController {

    private final MediaService mediaService;

    /**
     * Legacy endpoint - deprecated
     * @deprecated Use POST /sign instead
     */
    @GetMapping("/upload-signature")
    @Deprecated
    public ResponseEntity<?> getUploadSignature() {
        return ResponseEntity.ok(mediaService.getUploadSignature());
    }

    /**
     * Generate a signed upload signature for Cloudinary uploads.
     * This endpoint determines the correct folder structure based on context
     * and returns all required parameters for frontend to upload directly to Cloudinary.
     *
     * @param request MediaSignRequest containing context and identifier information
     * @return MediaSignResponse with signature, timestamp, and upload parameters
     */
    @PostMapping("/sign")
    public ResponseEntity<MediaSignResponse> generateUploadSignature(
            @Valid @RequestBody MediaSignRequest request) {
        MediaSignResponse response = mediaService.generateUploadSignature(request);
        return ResponseEntity.ok(response);
    }
}