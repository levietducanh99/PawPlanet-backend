package com.pawpplanet.backend.encyclopedia.repository;

import com.pawpplanet.backend.encyclopedia.entity.BreedEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BreedRepository extends JpaRepository<BreedEntity, Long> {
}