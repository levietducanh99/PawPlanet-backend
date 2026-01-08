package com.pawpplanet.backend.pet.service;

import com.pawpplanet.backend.pet.dto.*;

import java.util.List;

public interface PetService {
    PetProfileDTO createPet(CreatePetRequestDTO request);

    PetProfileDTO getPetById(Long petId);

    PetProfileDTO updatePet(Long petId, UpdatePetRequestDTO request);

    AddPetMediaResponse addMediaToGallery(Long petId, AddPetMediaRequest request);

    List<AllPetsResponseDTO> getAllMyPets();

}