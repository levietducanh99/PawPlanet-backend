package com.pawpplanet.backend.encyclopedia.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "encyclopedia_sections", schema = "encyclopedia")
public class EncyclopediaSectionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String code;

    private String displayName;

    private String description;

    public EncyclopediaSectionEntity() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
