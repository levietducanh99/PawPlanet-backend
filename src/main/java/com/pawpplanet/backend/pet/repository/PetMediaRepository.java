package com.pawpplanet.backend.pet.repository;

import com.pawpplanet.backend.pet.entity.PetMediaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PetMediaRepository extends JpaRepository<PetMediaEntity, Long> {
    // Return a list to safely handle unexpected duplicates in DB
    List<PetMediaEntity> findByPetIdAndDisplayOrder(Long petId, Integer displayOrder);
    List<PetMediaEntity> findByPetId(Long petId);
}
