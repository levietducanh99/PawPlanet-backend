package com.pawpplanet.backend.pet.service;

import com.pawpplanet.backend.pet.dto.CreatePetRequestDTO;
import com.pawpplanet.backend.pet.dto.PetProfileDTO;
import com.pawpplanet.backend.pet.dto.UpdatePetRequestDTO;

public interface PetService {
    PetProfileDTO createPet(CreatePetRequestDTO request);

    PetProfileDTO getPetById(Long petId);

     PetProfileDTO updatePet(Long petId, UpdatePetRequestDTO request);
}