package com.pawpplanet.backend.pet.repository;

import com.pawpplanet.backend.pet.entity.PetEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PetRepository extends JpaRepository<PetEntity, Long> {
}
