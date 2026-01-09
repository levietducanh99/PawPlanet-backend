package com.pawpplanet.backend.post.repository;

import com.pawpplanet.backend.post.entity.PostEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface PostRepository extends JpaRepository<PostEntity, Long> {

    @Query("SELECT p FROM PostEntity p JOIN PostPetEntity pp ON p.id = pp.postId WHERE pp.petId = :petId AND p.isDeleted = false ORDER BY p.createdAt DESC")
    List<PostEntity> findAllByPetId(@Param("petId") Long petId);

    List<PostEntity> findByAuthorIdOrderByCreatedAtDesc(Long authorId);

    List<PostEntity> findByAuthorIdInOrderByCreatedAtDesc(List<Long> authorIds);

    List<PostEntity> findByTypeOrderByCreatedAtDesc(String type);

    Long countByType(String type);


}
