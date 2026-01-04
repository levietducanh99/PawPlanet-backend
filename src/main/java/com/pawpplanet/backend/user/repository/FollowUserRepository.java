package com.pawpplanet.backend.user.repository;

import com.pawpplanet.backend.user.entity.FollowUser;
import com.pawpplanet.backend.user.entity.FollowUserId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FollowUserRepository extends JpaRepository<FollowUser, FollowUserId> {
    int countByIdFollowerId(Long followerId);
    int countByIdFollowingId(Long followingId);

    // Find follow relations where given user is the follower (i.e., users this user is following)
    List<FollowUser> findByIdFollowerId(Long followerId);

    // Find follow relations where given user is being followed (i.e., followers of this user)
    List<FollowUser> findByIdFollowingId(Long followingId);

    // Optimized: Check if user1 follows user2 (single query)
    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END " +
           "FROM FollowUser f " +
           "WHERE f.id.followerId = :followerId AND f.id.followingId = :followingId")
    boolean existsFollow(@Param("followerId") Long followerId, @Param("followingId") Long followingId);
}
