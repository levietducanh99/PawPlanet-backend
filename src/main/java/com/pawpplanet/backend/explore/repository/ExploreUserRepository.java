package com.pawpplanet.backend.explore.repository;

import com.pawpplanet.backend.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface ExploreUserRepository extends JpaRepository<UserEntity, Long> {

    /**
     * Fetch random users with minimal data for explore (optimized)
     * Includes pet count and follower count in single query
     */
    @Query(value = "SELECT " +
            "u.id as userId, " +
            "u.username, " +
            "u.avatar_url as avatarUrl, " +
            "(SELECT COUNT(*) FROM pet.pets p WHERE p.owner_id = u.id AND p.is_deleted = false) as petCount, " +
            "(SELECT COUNT(*) FROM auth.follow_user fu WHERE fu.following_id = u.id) as followerCount " +
            "FROM auth.users u " +
            "WHERE u.deleted_at IS NULL " +
            "AND u.is_verified = true " +
            "AND (:currentUserId IS NULL OR u.id != :currentUserId) " +
            "ORDER BY MOD(u.id + :seed, 999983) " +
            "LIMIT :limit",
            nativeQuery = true)
    List<Map<String, Object>> findRandomUsersOptimized(
            @Param("seed") Long seed,
            @Param("limit") int limit,
            @Param("currentUserId") Long currentUserId
    );
}

