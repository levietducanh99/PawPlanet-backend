package com.pawpplanet.backend.user.repository;

import com.pawpplanet.backend.user.entity.FollowUser;
import com.pawpplanet.backend.user.entity.FollowUserId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FollowUserRepository extends JpaRepository<FollowUser, FollowUserId> {
    @Query("SELECT COUNT(f) FROM FollowUser f JOIN UserEntity u1 ON f.id.followerId = u1.id JOIN UserEntity u2 ON f.id.followingId = u2.id WHERE f.id.followerId = :followerId AND u1.deletedAt IS NULL AND u2.deletedAt IS NULL")
    int countByIdFollowerId(@Param("followerId") Long followerId);

    @Query("SELECT COUNT(f) FROM FollowUser f JOIN UserEntity u1 ON f.id.followerId = u1.id JOIN UserEntity u2 ON f.id.followingId = u2.id WHERE f.id.followingId = :followingId AND u1.deletedAt IS NULL AND u2.deletedAt IS NULL")
    int countByIdFollowingId(@Param("followingId") Long followingId);

    // Find follow relations where given user is the follower (i.e., users this user is following)
    @Query("SELECT f FROM FollowUser f JOIN UserEntity u ON f.id.followingId = u.id WHERE f.id.followerId = :followerId AND u.deletedAt IS NULL")
    List<FollowUser> findByIdFollowerId(@Param("followerId") Long followerId);

    // Find follow relations where given user is being followed (i.e., followers of this user)
    @Query("SELECT f FROM FollowUser f JOIN UserEntity u ON f.id.followerId = u.id WHERE f.id.followingId = :followingId AND u.deletedAt IS NULL")
    List<FollowUser> findByIdFollowingId(@Param("followingId") Long followingId);

    // Optimized: Check if user1 follows user2 (single query)
    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END " +
           "FROM FollowUser f " +
           "JOIN UserEntity u1 ON f.id.followerId = u1.id " +
           "JOIN UserEntity u2 ON f.id.followingId = u2.id " +
           "WHERE f.id.followerId = :followerId AND f.id.followingId = :followingId " +
           "AND u1.deletedAt IS NULL AND u2.deletedAt IS NULL")
    boolean existsFollow(@Param("followerId") Long followerId, @Param("followingId") Long followingId);

    @Query("SELECT f.id.followingId FROM FollowUser f JOIN UserEntity u ON f.id.followingId = u.id WHERE f.id.followerId = :followerId AND u.deletedAt IS NULL")
    List<Long> findFollowingIdsByFollowerId(@Param("followerId") Long followerId);
}
