package com.pawpplanet.backend.pet.entity;

import com.pawpplanet.backend.encyclopedia.entity.BreedEntity;
import com.pawpplanet.backend.user.entity.UserEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "pets", schema = "pet")
public class PetEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String species;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "breed_id")
    private BreedEntity breed;

    private Integer age;

    private String gender;

    private String healthStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private UserEntity owner;

    public PetEntity() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSpecies() { return species; }
    public void setSpecies(String species) { this.species = species; }

    public BreedEntity getBreed() { return breed; }
    public void setBreed(BreedEntity breed) { this.breed = breed; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getHealthStatus() { return healthStatus; }
    public void setHealthStatus(String healthStatus) { this.healthStatus = healthStatus; }

    public UserEntity getOwner() { return owner; }
    public void setOwner(UserEntity owner) { this.owner = owner; }
}
