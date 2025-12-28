package com.pawpplanet.backend.pet.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "pet_media", schema = "pet")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PetMediaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pet_id")
    private Long petId;

    private String type;  // image | video

    private String role;  // avatar | gallery

    @Column(columnDefinition = "TEXT")
    private String url;

    @Column(name = "display_order")
    private Integer displayOrder;
}
