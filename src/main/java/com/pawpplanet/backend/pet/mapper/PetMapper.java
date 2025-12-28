package com.pawpplanet.backend.pet.mapper;

import com.pawpplanet.backend.pet.dto.CreatePetRequestDTO;
import com.pawpplanet.backend.pet.dto.PetProfileDTO;
import com.pawpplanet.backend.pet.entity.PetEntity;

public class PetMapper {
    public static PetEntity toEntity(CreatePetRequestDTO req) {
        PetEntity e = new PetEntity();
        e.setName(req.getName());
        e.setSpeciesId(req.getSpeciesId());
        e.setBreedId(req.getBreedId());
        e.setBirthDate(req.getBirthDate());
        e.setGender(req.getGender());
        e.setDescription(req.getDescription());
        e.setStatus(req.getStatus());
        // ownerId left null here; set from authenticated user if available
        return e;
    }

    public static PetProfileDTO toDto(PetEntity e) {
        if (e == null) return null;
        PetProfileDTO dto = new PetProfileDTO();
        dto.setId(e.getId());
        dto.setName(e.getName());
        dto.setSpeciesId(e.getSpeciesId());
        dto.setBreedId(e.getBreedId());
        dto.setBirthDate(e.getBirthDate());
        dto.setGender(e.getGender());
        dto.setDescription(e.getDescription());
        dto.setStatus(e.getStatus());
        dto.setOwnerId(e.getOwnerId());
        // speciesName, breedName, ownerUsername not populated here
        return dto;
    }
}
