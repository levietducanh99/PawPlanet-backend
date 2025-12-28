package com.pawpplanet.backend.user.repository;

import com.pawpplanet.backend.user.entity.FollowUserEntity;
import com.pawpplanet.backend.user.entity.FollowUserId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowUserRepository
        extends JpaRepository<FollowUserEntity, FollowUserId> {

    int countByIdFollowerId(Long followerId);

    int countByIdFollowingId(Long followingId);
}

