// src/main/java/com/pawpplanet/backend/pet/controller/PetAdoptionProfileController.java
package com.pawpplanet.backend.pet.controller;

import com.pawpplanet.backend.pet.dto.PetAdoptionProfileDto;
import com.pawpplanet.backend.pet.service.PetAdoptionProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/pets")
@RequiredArgsConstructor
public class PetAdoptionProfileController {

    private final PetAdoptionProfileService petAdoptionProfileService;



    @PostMapping("/{petId}/adoption-profile")
    public ResponseEntity<PetAdoptionProfileDto> createProfile(
            @PathVariable("petId") Long petId,
            @RequestBody PetAdoptionProfileDto dto) {
        PetAdoptionProfileDto created = petAdoptionProfileService.createOrUpdate(petId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{petId}/adoption-profile")
    public ResponseEntity<PetAdoptionProfileDto> getProfile(@PathVariable("petId") Long petId) {
        PetAdoptionProfileDto dto = petAdoptionProfileService.getByPetId(petId);
        return ResponseEntity.ok(dto);
    }
}
