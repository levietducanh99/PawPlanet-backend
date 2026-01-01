package com.pawpplanet.backend.encyclopedia.repository;

import com.pawpplanet.backend.encyclopedia.entity.BreedEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BreedRepository extends JpaRepository<BreedEntity, Long> {

    // Find breeds by taxonomy type
    List<BreedEntity> findByTaxonomyType(String taxonomyType);

    // Find breeds by species and taxonomy type
    List<BreedEntity> findBySpeciesIdAndTaxonomyType(Long speciesId, String taxonomyType);
}