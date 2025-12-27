package com.pawpplanet.backend.encyclopedia.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "breed_attributes", schema = "encyclopedia")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BreedAttributeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "breed_id")
    private Long breedId;

    @Column(name = "key", nullable = false)
    private String key;

    private String value;

    @Column(name = "display_order")
    private Integer displayOrder;
}

