package com.pawpplanet.backend.pet.controller;

import com.pawpplanet.backend.pet.dto.*;
import com.pawpplanet.backend.pet.service.PetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pets")
@RequiredArgsConstructor
public class     PetController {

    private final PetService petService;

    // Tạo mới thú cưng
    @PostMapping
    public ResponseEntity<PetProfileDTO> createPet(@RequestBody CreatePetRequestDTO request) {
        return ResponseEntity.ok(petService.createPet(request));
    }

    // Xem chi tiết hồ sơ thú cưng
    @GetMapping("/{id}")
    public ResponseEntity<PetProfileDTO> getPetById(@PathVariable Long id) {
        return ResponseEntity.ok(petService.getPetById(id));
    }

    // Cập nhật thông tin/avatar thú cưng
    @PutMapping("/{id}")
    public ResponseEntity<PetProfileDTO> updatePet(
            @PathVariable Long id,
            @RequestBody UpdatePetRequestDTO request
    ) {
        return ResponseEntity.ok(petService.updatePet(id, request));
    }

    @GetMapping("/my-pets")
    public ResponseEntity<List<AllPetsResponseDTO>> getAllMyPets() {
        List<AllPetsResponseDTO> pets = petService.getAllMyPets();
        return ResponseEntity.ok(pets);
    }

    /**
     * Add photos/videos to pet gallery
     * Called after frontend has successfully uploaded to Cloudinary
     *
     * @param id The pet ID
     * @param request Request containing media URLs from Cloudinary
     * @return Response with added media details
     */
    @PostMapping("/{id}/gallery")
    public ResponseEntity<AddPetMediaResponse> addMediaToGallery(
            @PathVariable Long id,
            @Valid @RequestBody AddPetMediaRequest request
    ) {
        return ResponseEntity.ok(petService.addMediaToGallery(id, request));
    }
}