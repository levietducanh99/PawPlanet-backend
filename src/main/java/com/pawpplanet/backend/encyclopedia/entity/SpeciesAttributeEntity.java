package com.pawpplanet.backend.encyclopedia.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "species_attributes", schema = "encyclopedia")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpeciesAttributeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "species_id")
    private Long speciesId;

    @Column(name = "key", nullable = false)
    private String key;

    @Column(name = "value_min")
    private BigDecimal valueMin;

    @Column(name = "value_max")
    private BigDecimal valueMax;

    private String unit;

    @Column(name = "display_order")
    private Integer displayOrder;
}

