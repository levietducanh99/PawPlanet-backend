package com.pawpplanet.backend.encyclopedia.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "media", schema = "encyclopedia")
@Data
@NoArgsConstructor
@AllArgsConstructor
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
}

