// src/main/java/com/pawpplanet/backend/pet/repository/PetAdoptionProfileRepository.java
package com.pawpplanet.backend.pet.repository;

import com.pawpplanet.backend.pet.entity.PetAdoptionProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PetAdoptionProfileRepository extends JpaRepository<PetAdoptionProfileEntity, Long> {
}
