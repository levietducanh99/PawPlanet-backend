package com.pawpplanet.backend.encyclopedia.repository;

import com.pawpplanet.backend.encyclopedia.entity.BreedSectionEntity;
import com.pawpplanet.backend.encyclopedia.entity.BreedSectionId;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BreedSectionRepository extends JpaRepository<BreedSectionEntity, BreedSectionId> {
    List<BreedSectionEntity> findByIdBreedId(Long breedId);
    List<BreedSectionEntity> findByIdSectionId(Long sectionId);
}

