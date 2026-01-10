package com.pawpplanet.backend.pet.repository;

import com.pawpplanet.backend.pet.entity.PetEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PetRepository extends JpaRepository<PetEntity, Long> {

    int countByOwnerId(Long ownerId);

    List<PetEntity> findByOwnerId(Long ownerId);

    @Query("SELECT DISTINCT p FROM PetEntity p " +
           "LEFT JOIN BreedEntity b ON p.breedId = b.id " +
           "WHERE (LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(b.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "p.isDeleted = false")
    List<PetEntity> searchByNameOrBreed(@Param("keyword") String keyword, Pageable pageable);
}
