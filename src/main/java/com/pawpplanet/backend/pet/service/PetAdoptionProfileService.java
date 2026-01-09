package com.pawpplanet.backend.pet.service;

import com.pawpplanet.backend.pet.dto.PetAdoptionProfileDto;
import com.pawpplanet.backend.pet.dto.PetProfileDTO;

public interface PetAdoptionProfileService {
    PetAdoptionProfileDto createOrUpdate(Long petId, PetAdoptionProfileDto dto);

    PetAdoptionProfileDto getByPetId(Long petId);
}
