package com.pawpplanet.backend.encyclopedia.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "breed_section_contents", schema = "encyclopedia")
public class BreedSectionContentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "breed_id")
    private BreedEntity breed;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id")
    private EncyclopediaSectionEntity section;

    private String language;

    @Column(columnDefinition = "text")
    private String content;

    public BreedSectionContentEntity() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public BreedEntity getBreed() { return breed; }
    public void setBreed(BreedEntity breed) { this.breed = breed; }

    public EncyclopediaSectionEntity getSection() { return section; }
    public void setSection(EncyclopediaSectionEntity section) { this.section = section; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
