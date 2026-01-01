package com.pawpplanet.backend.pet.mapper;

import com.pawpplanet.backend.pet.dto.CreatePetRequestDTO;
import com.pawpplanet.backend.pet.dto.PetMediaDTO;
import com.pawpplanet.backend.pet.dto.PetProfileDTO;
import com.pawpplanet.backend.pet.entity.PetEntity;
import com.pawpplanet.backend.pet.entity.PetMediaEntity;

import java.util.List;

public class PetMapper {

    /* ================= CREATE ================= */

    public static PetEntity toEntity(CreatePetRequestDTO req) {
        if (req == null) return null;

        PetEntity e = new PetEntity();
        e.setName(req.getName());
        e.setSpeciesId(req.getSpeciesId());
        e.setBreedId(req.getBreedId());
        e.setBirthDate(req.getBirthDate());
        e.setGender(req.getGender());
        e.setDescription(req.getDescription());
        e.setStatus(req.getStatus());
        // ownerId set elsewhere (from authenticated user)
        return e;
    }

    /* ================= BASIC DTO ================= */

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
        return dto;
    }

    /* ================= PROFILE DTO ================= */

    public static PetProfileDTO toProfileDTO(
            PetEntity pet,
            List<PetMediaEntity> mediaList
    ) {
        if (pet == null) return null;

        PetProfileDTO dto = toDto(pet);

        if (mediaList != null) {
            dto.setMedia(
                    mediaList.stream()
                            .map(PetMapper::toMediaDTO)
                            .toList()
            );
        }

        return dto;
    }

    /* ================= MEDIA ================= */

    public static PetMediaDTO toMediaDTO(PetMediaEntity entity) {
        if (entity == null) return null;

        PetMediaDTO dto = new PetMediaDTO();
        dto.setId(entity.getId());
        dto.setType(entity.getType());
        dto.setRole(entity.getRole());
        dto.setUrl(entity.getUrl());
        dto.setDisplayOrder(entity.getDisplayOrder());
        return dto;
    }
}
