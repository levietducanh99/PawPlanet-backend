package com.pawpplanet.backend.pet.repository;

import com.pawpplanet.backend.pet.entity.FollowPetEntity;
import com.pawpplanet.backend.pet.entity.FollowPetId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface FollowPetRepository extends JpaRepository<FollowPetEntity, FollowPetId> {
    @Query("SELECT f FROM FollowPetEntity f JOIN PetEntity p ON f.id.petId = p.id WHERE f.id.petId = :petId AND p.isDeleted = false")
    List<FollowPetEntity> findByIdPetId(@Param("petId") Long petId);

    @Query("SELECT f FROM FollowPetEntity f JOIN PetEntity p ON f.id.petId = p.id WHERE f.id.userId = :userId AND p.isDeleted = false")
    List<FollowPetEntity> findByIdUserId(@Param("userId") Long userId);
}

