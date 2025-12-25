package com.pawpplanet.backend.post.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "post_media", schema = "social")
public class PostMediaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private PostEntity post;

    private String type;

    private String url;

    public PostMediaEntity() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public PostEntity getPost() { return post; }
    public void setPost(PostEntity post) { this.post = post; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
}
