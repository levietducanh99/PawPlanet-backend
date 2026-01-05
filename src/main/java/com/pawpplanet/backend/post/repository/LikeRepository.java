package com.pawpplanet.backend.post.repository;

import com.pawpplanet.backend.post.entity.LikeEntity;
import com.pawpplanet.backend.post.entity.LikeId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LikeRepository extends JpaRepository<LikeEntity, LikeId> {

    int countByPostId(Long postId);

    boolean existsByPostIdAndUserId(Long postId, Long userId);

    void deleteByPostIdAndUserId(Long postId, Long userId);

    List<LikeEntity> findByPostId(Long postId);
}
