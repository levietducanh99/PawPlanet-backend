package com.pawpplanet.backend.pet.controller;

import com.pawpplanet.backend.pet.service.PetMediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/pets/media")
@RequiredArgsConstructor
public class PetMediaController {

    private final PetMediaService petMediaService;

    // Lấy chữ ký để Client upload ảnh trực tiếp lên Cloudinary
    @GetMapping("/upload-signature")
    public ResponseEntity<?> getUploadSignature() {
        return ResponseEntity.ok(petMediaService.getUploadSignature());
    }


}