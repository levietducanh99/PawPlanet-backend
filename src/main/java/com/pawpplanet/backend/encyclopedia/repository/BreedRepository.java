package com.pawpplanet.backend.encyclopedia.repository;

import com.pawpplanet.backend.encyclopedia.entity.BreedEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface BreedRepository extends JpaRepository<BreedEntity, Long> {
}