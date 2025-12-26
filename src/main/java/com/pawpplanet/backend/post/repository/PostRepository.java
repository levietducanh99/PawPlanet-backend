package com.pawpplanet.backend.post.repository;

import com.pawpplanet.backend.post.entity.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<PostEntity, Long> {
}
