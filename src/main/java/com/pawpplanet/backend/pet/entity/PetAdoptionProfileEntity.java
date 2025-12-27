package com.pawpplanet.backend.pet.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "pet_adoption_profiles", schema = "pet")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PetAdoptionProfileEntity {

    @Id
    @Column(name = "pet_id")
    private Long petId;

    @Column(name = "health_status", columnDefinition = "TEXT")
    private String healthStatus;

    private Boolean vaccinated;

    private Boolean sterilized;

    @Column(columnDefinition = "TEXT")
    private String personality;

    @Column(columnDefinition = "TEXT")
    private String habits;

    @Column(name = "favorite_activities", columnDefinition = "TEXT")
    private String favoriteActivities;

    @Column(name = "care_instructions", columnDefinition = "TEXT")
    private String careInstructions;

    @Column(columnDefinition = "TEXT")
    private String diet;

    @Column(name = "adoption_requirements", columnDefinition = "TEXT")
    private String adoptionRequirements;

    @Column(name = "reason_for_adoption", columnDefinition = "TEXT")
    private String reasonForAdoption;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

