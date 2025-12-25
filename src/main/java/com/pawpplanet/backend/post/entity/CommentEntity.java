package com.pawpplanet.backend.post.entity;

import com.pawpplanet.backend.user.entity.UserEntity;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "comments", schema = "social")
public class CommentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private PostEntity post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private CommentEntity parent;

    @Column(columnDefinition = "text")
    private String content;

    private Instant createdAt;

    public CommentEntity() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public PostEntity getPost() { return post; }
    public void setPost(PostEntity post) { this.post = post; }

    public UserEntity getUser() { return user; }
    public void setUser(UserEntity user) { this.user = user; }

    public CommentEntity getParent() { return parent; }
    public void setParent(CommentEntity parent) { this.parent = parent; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
