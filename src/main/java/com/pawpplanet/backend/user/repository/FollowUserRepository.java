package com.pawpplanet.backend.user.repository;

import com.pawpplanet.backend.user.entity.FollowUserEntity;
import com.pawpplanet.backend.user.entity.FollowUserId;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FollowUserRepository extends JpaRepository<FollowUserEntity, FollowUserId> {
    List<FollowUserEntity> findByIdFollowerId(Long followerId);
    List<FollowUserEntity> findByIdFollowingId(Long followingId);
}
