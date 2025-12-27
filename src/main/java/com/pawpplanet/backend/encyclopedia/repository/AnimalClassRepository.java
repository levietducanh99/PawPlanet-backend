package com.pawpplanet.backend.encyclopedia.repository;

import com.pawpplanet.backend.encyclopedia.entity.AnimalClassEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AnimalClassRepository extends JpaRepository<AnimalClassEntity, Long> {

    Optional<AnimalClassEntity> findByCode(String code);

    boolean existsByCode(String code);
}

