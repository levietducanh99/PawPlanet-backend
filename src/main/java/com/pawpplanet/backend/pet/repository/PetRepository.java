package com.pawpplanet.backend.pet.repository;

import com.pawpplanet.backend.pet.entity.PetEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PetRepository extends JpaRepository<PetEntity, Long> {

    int countByOwnerId(Long ownerId);

    List<PetEntity> findByOwnerId(Long ownerId);
}
