package com.pawpplanet.backend.encyclopedia.repository;

import com.pawpplanet.backend.encyclopedia.entity.BreedAttributeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BreedAttributeRepository extends JpaRepository<BreedAttributeEntity, Long> {
    Page<BreedAttributeEntity> findByBreedId(Long breedId, Pageable pageable);
    List<BreedAttributeEntity> findByBreedIdOrderByDisplayOrderAsc(Long breedId);
}

