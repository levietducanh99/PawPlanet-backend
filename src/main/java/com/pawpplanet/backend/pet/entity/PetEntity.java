package com.pawpplanet.backend.pet.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "pets", schema = "pet")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Where(clause = "is_deleted = false")
public class PetEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name = "species_id")
    private Long speciesId;

    @Column(name = "breed_id")
    private Long breedId;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    private String gender;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String status;  // owned | for_adoption | private

    @Column(name = "owner_id")
    private Long ownerId;

    private BigDecimal weight;

    private BigDecimal height;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "deleted_by")
    private Long deletedBy;
}