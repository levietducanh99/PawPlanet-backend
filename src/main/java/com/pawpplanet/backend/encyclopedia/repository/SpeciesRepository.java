package com.pawpplanet.backend.encyclopedia.repository;

import com.pawpplanet.backend.encyclopedia.entity.SpeciesEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpeciesRepository extends JpaRepository<SpeciesEntity, Long> {
    Page<SpeciesEntity> findByClassId(Long classId, Pageable pageable);

    // Non-paged finder for listing all species under a class
    List<SpeciesEntity> findByClassId(Long classId);

    Optional<SpeciesEntity> findBySlug(String slug);

    boolean existsBySlug(String slug);

    // Search by keyword in name or scientific name (paged)
    Page<SpeciesEntity> findByNameContainingIgnoreCaseOrScientificNameContainingIgnoreCase(String name, String scientificName, Pageable pageable);

    // Non-paged search used by cross-entity search aggregator
    List<SpeciesEntity> findByNameContainingIgnoreCaseOrScientificNameContainingIgnoreCase(String name, String scientificName);
}