package com.pawpplanet.backend.encyclopedia.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

@Entity
@Table(name = "media", schema = "encyclopedia")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Where(clause = "is_deleted = false")
public class MediaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "entity_type")
    private String entityType;  // class | species | breed

    @Column(name = "entity_id")
    private Long entityId;

    private String type;  // image | video

    private String role;  // hero | gallery | avatar

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

