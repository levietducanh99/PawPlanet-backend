package com.pawpplanet.backend.post.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "post_media", schema = "social")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostMediaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "post_id")
    private Long postId;

    private String type;  // image | video

    @Column(columnDefinition = "TEXT")
    private String url;

    @Column(name = "display_order")
    private Integer displayOrder;
}
