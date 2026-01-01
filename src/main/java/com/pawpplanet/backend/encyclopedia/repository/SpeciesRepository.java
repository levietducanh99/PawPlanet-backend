package com.pawpplanet.backend.encyclopedia.repository;

import com.pawpplanet.backend.encyclopedia.entity.SpeciesEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface SpeciesRepository extends JpaRepository<SpeciesEntity, Long> {
    Page<SpeciesEntity> findByClassId(Long classId, Pageable pageable);
}