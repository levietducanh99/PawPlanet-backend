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

    @PostMapping
    public ResponseEntity<PetProfileDTO> createPet(@RequestBody CreatePetRequestDTO request) {
        return ResponseEntity.ok(petService.createPet(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PetProfileDTO> getPetById(@PathVariable Long id) {
        return ResponseEntity.ok(petService.getPetById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PetProfileDTO> updatePet(
            @PathVariable Long id,
            @RequestBody UpdatePetRequestDTO request
    ) {
        return ResponseEntity.ok(petService.updatePet(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePet(@PathVariable Long id) {
        petService.deletePet(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my-pets")
    public ResponseEntity<List<AllPetsResponseDTO>> getAllMyPets() {
        List<AllPetsResponseDTO> pets = petService.getAllMyPets();
        return ResponseEntity.ok(pets);
    }

    @GetMapping("/my-pets/{id}")
    public ResponseEntity<List<AllPetsResponseDTO>> getAllUserPets(@PathVariable Long id) {
        List<AllPetsResponseDTO> pets = petService.getAllUserPets(id);
        return ResponseEntity.ok(pets);
    }

    @PostMapping("/{id}/gallery")
    public ResponseEntity<AddPetMediaResponse> addMediaToGallery(
            @PathVariable Long id,
            @Valid @RequestBody AddPetMediaRequest request
    ) {
        return ResponseEntity.ok(petService.addMediaToGallery(id, request));
    }
}