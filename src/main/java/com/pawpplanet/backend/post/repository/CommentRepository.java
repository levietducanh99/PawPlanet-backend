package com.pawpplanet.backend.post.repository;

import com.pawpplanet.backend.post.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
}
