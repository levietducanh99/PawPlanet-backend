package com.pawpplanet.backend.encyclopedia.repository;

import com.pawpplanet.backend.encyclopedia.entity.BreedEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BreedRepository extends JpaRepository<BreedEntity, Long> {

    // Find breeds by taxonomy type
    List<BreedEntity> findByTaxonomyType(String taxonomyType);

    // Find breeds by species and taxonomy type
    List<BreedEntity> findBySpeciesIdAndTaxonomyType(Long speciesId, String taxonomyType);

    // Non-paged finder for species
    List<BreedEntity> findBySpeciesId(Long speciesId);

    // Paged finders
    Page<BreedEntity> findBySpeciesId(Long speciesId, Pageable pageable);
    Page<BreedEntity> findByTaxonomyType(String taxonomyType, Pageable pageable);
    Page<BreedEntity> findBySpeciesIdAndTaxonomyType(Long speciesId, String taxonomyType, Pageable pageable);

    // Slug methods
    Optional<BreedEntity> findBySlug(String slug);

    boolean existsBySlug(String slug);

    // Search by name (paged and non-paged)
    Page<BreedEntity> findByNameContainingIgnoreCase(String name, Pageable pageable);
    List<BreedEntity> findByNameContainingIgnoreCase(String name);
}
