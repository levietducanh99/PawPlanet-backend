package com.pawpplanet.backend.post.repository;

import com.pawpplanet.backend.post.entity.PostMediaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostMediaRepository extends JpaRepository<PostMediaEntity, Long> {

    List<PostMediaEntity> findByPostIdOrderByDisplayOrder(Long postId);
}

