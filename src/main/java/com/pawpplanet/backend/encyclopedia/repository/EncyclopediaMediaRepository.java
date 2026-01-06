package com.pawpplanet.backend.encyclopedia.repository;

import com.pawpplanet.backend.encyclopedia.entity.EncyclopediaMediaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EncyclopediaMediaRepository extends JpaRepository<EncyclopediaMediaEntity, Long> {

    // Lấy thumbnail (avatar) cho entity
    Optional<EncyclopediaMediaEntity> findFirstByEntityTypeAndEntityIdAndRole(
            String entityType, Long entityId, String role);

    // Lấy tất cả gallery media với pagination
    @Query("SELECT m FROM EncyclopediaMediaEntity m WHERE m.entityType = :entityType AND m.entityId = :entityId AND m.role = 'gallery' ORDER BY m.displayOrder ASC")
    Page<EncyclopediaMediaEntity> findGalleryByEntityTypeAndEntityId(
            @Param("entityType") String entityType,
            @Param("entityId") Long entityId,
            Pageable pageable);

    // Lấy vài ảnh gallery đầu tiên (cho preview trong detail)
    @Query("SELECT m FROM EncyclopediaMediaEntity m WHERE m.entityType = :entityType AND m.entityId = :entityId AND m.role = 'gallery' ORDER BY m.displayOrder ASC")
    List<EncyclopediaMediaEntity> findTopGalleryByEntityTypeAndEntityId(
            @Param("entityType") String entityType,
            @Param("entityId") Long entityId,
            Pageable pageable);

    // Lấy tất cả media của entity
    List<EncyclopediaMediaEntity> findByEntityTypeAndEntityIdOrderByDisplayOrderAsc(
            String entityType, Long entityId);

    // Get max display order for gallery items (used when adding new media)
    @Query("SELECT COALESCE(MAX(m.displayOrder), 0) FROM EncyclopediaMediaEntity m WHERE m.entityType = :entityType AND m.entityId = :entityId AND m.role = :role")
    Optional<Integer> findMaxDisplayOrderByEntityTypeAndEntityIdAndRole(
            @Param("entityType") String entityType,
            @Param("entityId") Long entityId,
            @Param("role") String role);
}

