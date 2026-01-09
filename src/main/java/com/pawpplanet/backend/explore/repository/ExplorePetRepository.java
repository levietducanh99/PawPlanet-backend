package com.pawpplanet.backend.explore.repository;

import com.pawpplanet.backend.pet.entity.PetEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface ExplorePetRepository extends JpaRepository<PetEntity, Long> {

    /**
     * Fetch random pets with minimal data for explore (optimized)
     * Includes species/breed names and owner info via JOINs
     */
    @Query(value = "SELECT " +
            "p.id as petId, " +
            "p.name, " +
            "s.name as speciesName, " +
            "b.name as breedName, " +
            "u.id as ownerId, " +
            "u.username as ownerUsername, " +
            "(SELECT pm.url FROM pet.pet_media pm " +
            " WHERE pm.pet_id = p.id AND pm.is_deleted = false " +
            " ORDER BY pm.display_order LIMIT 1) as avatarUrl, " +
            "(SELECT COUNT(*) FROM pet.follow_pet fp WHERE fp.pet_id = p.id) as followerCount " +
            "FROM pet.pets p " +
            "JOIN auth.users u ON p.owner_id = u.id " +
            "LEFT JOIN encyclopedia.species s ON p.species_id = s.id " +
            "LEFT JOIN encyclopedia.breeds b ON p.breed_id = b.id " +
            "WHERE p.is_deleted = false " +
            "AND p.status != 'private' " +
            "AND u.deleted_at IS NULL " +
            "AND (:currentUserId IS NULL OR p.owner_id != :currentUserId) " +
            "ORDER BY MOD(p.id + :seed, 999983) " +
            "LIMIT :limit",
            nativeQuery = true)
    List<Map<String, Object>> findRandomPetsOptimized(
            @Param("seed") Long seed,
            @Param("limit") int limit,
            @Param("currentUserId") Long currentUserId
    );
}

