package com.pawpplanet.backend.post.repository;

import com.pawpplanet.backend.post.entity.PostPetEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostPetRepository extends JpaRepository<PostPetEntity, Long> {

    List<PostPetEntity> findByPostId(Long postId);

    List<PostPetEntity> findByPetId(Long petId);
}

