package com.pawpplanet.backend.pet.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

@Entity
@Table(name = "pet_media", schema = "pet")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Where(clause = "is_deleted = false")
public class PetMediaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pet_id")
    private Long petId;

    private String type;  // image | video

    private String role;  // avatar | gallery

    @Column(name = "public_id", columnDefinition = "TEXT")
    private String publicId;  // Cloudinary public_id for building URLs

    @Column(columnDefinition = "TEXT")
    private String url;

    @Column(name = "display_order")
    private Integer displayOrder;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "deleted_by")
    private Long deletedBy;
}
