package com.pawpplanet.backend.pet.controller;

import com.pawpplanet.backend.pet.dto.CreatePetRequestDTO;
import com.pawpplanet.backend.pet.dto.PetProfileDTO;
import com.pawpplanet.backend.pet.dto.UpdatePetRequestDTO;
import com.pawpplanet.backend.pet.service.PetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/pets")
@RequiredArgsConstructor
public class PetController {

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
}