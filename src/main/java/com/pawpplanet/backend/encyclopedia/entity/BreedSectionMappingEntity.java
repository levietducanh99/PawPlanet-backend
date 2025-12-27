package com.pawpplanet.backend.encyclopedia.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Entity
@Table(name = "breed_section_mapping", schema = "encyclopedia")
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(BreedSectionMappingId.class)
public class BreedSectionMappingEntity {

    @Id
    @Column(name = "breed_id")
    private Long breedId;

    @Id
    @Column(name = "section_id")
    private Long sectionId;

    @Column(name = "display_order")
    private Integer displayOrder;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class BreedSectionMappingId implements Serializable {
    private Long breedId;
    private Long sectionId;
}

