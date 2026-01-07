package com.pawpplanet.backend.post.repository;

import com.pawpplanet.backend.post.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    int countByPostIdAndDeletedAtIsNull(Long postId);

    List<CommentEntity> findByPostId(Long postId);

}
