package com.pawpplanet.backend.pet.mapper;

import com.pawpplanet.backend.pet.dto.PetMediaDTO;
import com.pawpplanet.backend.pet.dto.SaveMediaRequest;
import com.pawpplanet.backend.pet.entity.PetMediaEntity;

public class PetMediaMapper {

    public static PetMediaDTO toDTO(PetMediaEntity entity) {
        if (entity == null) return null;
        PetMediaDTO dto = new PetMediaDTO();
        dto.setId(entity.getId());
        dto.setType(entity.getType());
        dto.setRole(entity.getRole());
        dto.setUrl(entity.getUrl());
        dto.setDisplayOrder(entity.getDisplayOrder());
        return dto;
    }

    public static PetMediaEntity toEntity(SaveMediaRequest request) {
        if (request == null) return null;
        PetMediaEntity entity = new PetMediaEntity();
        entity.setUrl(request.getUrl());
        entity.setType(request.getType());
        entity.setRole(request.getRole());
        entity.setDisplayOrder(0);
        return entity;
    }
}
