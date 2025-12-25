package com.pawpplanet.backend.pet.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "pet_media", schema = "pet")
public class PetMediaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id")
    private PetEntity pet;

    private String type;

    private String url;

    public PetMediaEntity() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public PetEntity getPet() { return pet; }
    public void setPet(PetEntity pet) { this.pet = pet; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
}
