package com.pawpplanet.backend.pet.mapper;

import com.pawpplanet.backend.pet.dto.PetMediaDTO;
import com.pawpplanet.backend.pet.dto.PetProfileDTO;
import com.pawpplanet.backend.pet.entity.PetEntity;
import com.pawpplanet.backend.pet.entity.PetMediaEntity;

import java.util.List;

public class PetMapper {

    public static PetProfileDTO toProfileDTO(
            PetEntity pet,
            List<PetMediaEntity> mediaList
    ) {
        PetProfileDTO dto = new PetProfileDTO();

        dto.setId(pet.getId());
        dto.setName(pet.getName());
        dto.setSpeciesId(pet.getSpeciesId());
        dto.setBreedId(pet.getBreedId());
        dto.setBirthDate(pet.getBirthDate());
        dto.setGender(pet.getGender());
        dto.setDescription(pet.getDescription());
        dto.setStatus(pet.getStatus());
        dto.setOwnerId(pet.getOwnerId());

        dto.setMedia(
                mediaList.stream()
                        .map(PetMapper::toMediaDTO)
                        .toList()
        );

        return dto;
    }

    public static PetMediaDTO toMediaDTO(PetMediaEntity entity) {
        PetMediaDTO dto = new PetMediaDTO();
        dto.setId(entity.getId());
        dto.setType(entity.getType());
        dto.setRole(entity.getRole());
        dto.setUrl(entity.getUrl());
        dto.setDisplayOrder(entity.getDisplayOrder());
        return dto;
    }
}
