package com.pawpplanet.backend.pet.repository;

import com.pawpplanet.backend.pet.entity.PetMediaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PetMediaRepository extends JpaRepository<PetMediaEntity, Long> {
    Optional<PetMediaEntity> findByPetIdAndDisplayOrder(Long petId, Integer displayOrder);
    List<PetMediaEntity> findByPetId(Long petId);
}
