package com.pawpplanet.backend.explore.repository;

import com.pawpplanet.backend.post.entity.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface ExplorePostRepository extends JpaRepository<PostEntity, Long> {

    /**
     * Fetch random posts with minimal data for explore (optimized)
     * Returns only necessary fields in a Map to avoid loading full entities
     */
    @Query(value = "SELECT " +
            "p.id as postId, " +
            "p.content, " +
            "p.created_at as createdAt, " +
            "u.id as authorId, " +
            "u.username as authorUsername, " +
            "u.avatar_url as authorAvatarUrl, " +
            "(SELECT COUNT(*) FROM social.likes l WHERE l.post_id = p.id) as likeCount, " +
            "(SELECT COUNT(*) FROM social.comments c WHERE c.post_id = p.id AND c.is_deleted = false) as commentCount, " +
            "CASE WHEN :currentUserId IS NOT NULL AND EXISTS(" +
            "  SELECT 1 FROM social.likes l2 WHERE l2.post_id = p.id AND l2.user_id = :currentUserId" +
            ") THEN true ELSE false END as liked " +
            "FROM social.posts p " +
            "JOIN auth.users u ON p.author_id = u.id " +
            "WHERE p.is_deleted = false " +
            "AND u.deleted_at IS NULL " +
            "AND (:currentUserId IS NULL OR p.author_id != :currentUserId) " +
            "ORDER BY MOD(p.id + :seed, 999983) " +
            "LIMIT :limit",
            nativeQuery = true)
    List<Map<String, Object>> findRandomPostsOptimized(
            @Param("seed") Long seed,
            @Param("limit") int limit,
            @Param("currentUserId") Long currentUserId
    );

    /**
     * Get media URLs for multiple posts in one query (batch)
     */
    @Query(value = "SELECT " +
            "pm.post_id as postId, " +
            "pm.url, " +
            "pm.type, " +
            "pm.display_order as displayOrder " +
            "FROM social.post_media pm " +
            "WHERE pm.post_id IN :postIds " +
            "AND pm.is_deleted = false " +
            "ORDER BY pm.post_id, pm.display_order",
            nativeQuery = true)
    List<Map<String, Object>> findMediaByPostIds(@Param("postIds") List<Long> postIds);
}

