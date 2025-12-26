package com.pawpplanet.backend.encyclopedia.repository;

import com.pawpplanet.backend.encyclopedia.entity.SpeciesEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpeciesRepository extends JpaRepository<SpeciesEntity, Long> {
}

