package com.pawpplanet.backend.pet.controller;

import com.pawpplanet.backend.pet.dto.CreatePetRequestDTO;
import com.pawpplanet.backend.pet.dto.PetProfileDTO;
import com.pawpplanet.backend.pet.dto.UpdatePetRequestDTO;
import com.pawpplanet.backend.pet.service.PetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/pets")
@RequiredArgsConstructor
public class PetController {
    private final PetService petService;

    @PostMapping
    public ResponseEntity<PetProfileDTO> createPet(@RequestBody CreatePetRequestDTO request) {
        PetProfileDTO created = petService.createPet(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PetProfileDTO> updatePet(@PathVariable Long id,
                                                   @RequestBody UpdatePetRequestDTO dto) {
        PetProfileDTO updated = petService.updatePet(id, dto);
        return ResponseEntity.ok(updated);
    }
}
