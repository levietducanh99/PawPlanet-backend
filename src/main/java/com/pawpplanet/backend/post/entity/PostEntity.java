package com.pawpplanet.backend.post.entity;

import com.pawpplanet.backend.user.entity.UserEntity;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "posts", schema = "social")
public class PostEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private UserEntity author;

    @Column(columnDefinition = "text")
    private String content;

    @Column(columnDefinition = "text")
    private String hashtags; // comma-separated list

    private String type;

    private String contactInfo;

    private String location;

    private Instant createdAt;

    private Instant deletedAt;

    public PostEntity() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public UserEntity getAuthor() { return author; }
    public void setAuthor(UserEntity author) { this.author = author; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getHashtags() { return hashtags; }
    public void setHashtags(String hashtags) { this.hashtags = hashtags; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getContactInfo() { return contactInfo; }
    public void setContactInfo(String contactInfo) { this.contactInfo = contactInfo; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getDeletedAt() { return deletedAt; }
    public void setDeletedAt(Instant deletedAt) { this.deletedAt = deletedAt; }
}
