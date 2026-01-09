package com.pawpplanet.backend.pet.service.impl;

import com.pawpplanet.backend.pet.dto.PetAdoptionProfileDto;
import com.pawpplanet.backend.pet.entity.PetAdoptionProfileEntity;
import com.pawpplanet.backend.pet.repository.PetAdoptionProfileRepository;
import com.pawpplanet.backend.pet.service.PetAdoptionProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;


@Service
@Transactional
@RequiredArgsConstructor
public class PetAdoptionProfileServiceImpl implements PetAdoptionProfileService {

    private final PetAdoptionProfileRepository repository;

    @Override
    public PetAdoptionProfileDto createOrUpdate(Long petId, PetAdoptionProfileDto dto) {
        PetAdoptionProfileEntity entity = toEntity(dto);
        entity.setPetId(petId);
        PetAdoptionProfileEntity saved = repository.save(entity);
        return toDto(saved);
    }

    @Override
    public PetAdoptionProfileDto getByPetId(Long petId) {
        PetAdoptionProfileEntity entity = repository.findById(petId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Adoption profile not found"));
        return toDto(entity);
    }

    private PetAdoptionProfileDto toDto(PetAdoptionProfileEntity e) {
        if (e == null) return null;
        return new PetAdoptionProfileDto(
                e.getPetId(),
                e.getHealthStatus(),
                e.getVaccinated(),
                e.getSterilized(),
                e.getPersonality(),
                e.getHabits(),
                e.getFavoriteActivities(),
                e.getCareInstructions(),
                e.getDiet(),
                e.getAdoptionRequirements(),
                e.getReasonForAdoption(),
                e.getCreatedAt(),
                e.getUpdatedAt()
        );
    }

    private PetAdoptionProfileEntity toEntity(PetAdoptionProfileDto d) {
        if (d == null) return null;
        PetAdoptionProfileEntity e = new PetAdoptionProfileEntity();
        e.setPetId(d.getPetId());
        e.setHealthStatus(d.getHealthStatus());
        e.setVaccinated(d.getVaccinated());
        e.setSterilized(d.getSterilized());
        e.setPersonality(d.getPersonality());
        e.setHabits(d.getHabits());
        e.setFavoriteActivities(d.getFavoriteActivities());
        e.setCareInstructions(d.getCareInstructions());
        e.setDiet(d.getDiet());
        e.setAdoptionRequirements(d.getAdoptionRequirements());
        e.setReasonForAdoption(d.getReasonForAdoption());
        // createdAt/updatedAt will be handled by entity lifecycle callbacks
        return e;
    }


}
