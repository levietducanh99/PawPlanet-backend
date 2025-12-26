package com.pawpplanet.backend.encyclopedia.repository;

import com.pawpplanet.backend.encyclopedia.entity.BreedSectionContentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BreedSectionContentRepository extends JpaRepository<BreedSectionContentEntity, Long> {
    List<BreedSectionContentEntity> findByBreedId(Long breedId);
    List<BreedSectionContentEntity> findBySectionId(Long sectionId);
}

