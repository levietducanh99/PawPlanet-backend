package com.pawpplanet.backend.media.controller;

import com.pawpplanet.backend.media.service.MediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/media")
@RequiredArgsConstructor
public class MediaController {

    private final MediaService mediaService;

    // Lấy chữ ký để Client upload ảnh trực tiếp lên Cloudinary
    @GetMapping("/upload-signature")
    public ResponseEntity<?> getUploadSignature() {
        return ResponseEntity.ok(mediaService.getUploadSignature());
    }


}