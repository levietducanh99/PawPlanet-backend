package com.pawpplanet.backend.post.repository;

import com.pawpplanet.backend.post.entity.LikeEntity;
import com.pawpplanet.backend.post.entity.LikeId;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LikeRepository extends JpaRepository<LikeEntity, LikeId> {
    List<LikeEntity> findByIdUserId(Long userId);
    List<LikeEntity> findByIdPostId(Long postId);
}
