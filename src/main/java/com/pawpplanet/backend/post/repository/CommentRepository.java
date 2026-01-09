package com.pawpplanet.backend.post.repository;

import com.pawpplanet.backend.post.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    @Query("SELECT COUNT(c) FROM CommentEntity c JOIN PostEntity p ON c.postId = p.id WHERE c.postId = :postId AND c.isDeleted = false AND p.isDeleted = false")
    int countByPostId(@Param("postId") Long postId);

    @Query("SELECT c FROM CommentEntity c JOIN PostEntity p ON c.postId = p.id WHERE c.postId = :postId AND c.isDeleted = false AND p.isDeleted = false")
    List<CommentEntity> findByPostId(@Param("postId") Long postId);

}
