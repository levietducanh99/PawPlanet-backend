package com.pawpplanet.backend.pet.repository;

import com.pawpplanet.backend.pet.entity.FollowPetEntity;
import com.pawpplanet.backend.pet.entity.FollowPetId;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FollowPetRepository extends JpaRepository<FollowPetEntity, FollowPetId> {
    List<FollowPetEntity> findByIdPetId(Long petId);
    List<FollowPetEntity> findByIdUserId(Long userId);
}

