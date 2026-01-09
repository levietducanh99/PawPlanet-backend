package com.pawpplanet.backend.post.repository;

import com.pawpplanet.backend.post.entity.LikeEntity;
import com.pawpplanet.backend.post.entity.LikeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LikeRepository extends JpaRepository<LikeEntity, LikeId> {

    @Query("SELECT COUNT(l) FROM LikeEntity l JOIN PostEntity p ON l.postId = p.id WHERE l.postId = :postId AND p.isDeleted = false")
    int countByPostId(@Param("postId") Long postId);

    @Query("SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END FROM LikeEntity l JOIN PostEntity p ON l.postId = p.id WHERE l.postId = :postId AND l.userId = :userId AND p.isDeleted = false")
    boolean existsByPostIdAndUserId(@Param("postId") Long postId, @Param("userId") Long userId);

    void deleteByPostIdAndUserId(Long postId, Long userId);

    @Query("SELECT l FROM LikeEntity l JOIN PostEntity p ON l.postId = p.id WHERE l.postId = :postId AND p.isDeleted = false")
    List<LikeEntity> findByPostId(@Param("postId") Long postId);
}
