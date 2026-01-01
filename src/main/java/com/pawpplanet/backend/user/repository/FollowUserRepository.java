package com.pawpplanet.backend.user.repository;

import com.pawpplanet.backend.user.entity.FollowUser;
import com.pawpplanet.backend.user.entity.FollowUserId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FollowUserRepository extends JpaRepository<FollowUser, FollowUserId> {
    int countByIdFollowerId(Long followerId);
    int countByIdFollowingId(Long followingId);

    // Find follow relations where given user is the follower (i.e., users this user is following)
    List<FollowUser> findByIdFollowerId(Long followerId);

    // Find follow relations where given user is being followed (i.e., followers of this user)
    List<FollowUser> findByIdFollowingId(Long followingId);
}
