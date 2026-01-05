package com.pawpplanet.backend.post.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class PostResponse {

    private Long id;

    // Author
    private Long authorId;
    private String authorUsername;
    private String authorAvatarUrl;

    // Content
    private String content;
    private String hashtags;
    private String type;
    private String contactInfo;
    private String location;

    // Interaction
    private int likeCount;
    private int commentCount;
    private boolean liked;

    private LocalDateTime createdAt;

    // Media + Pet
    private List<PostMediaDTO> media;
    private List<PostPetDTO> pets;

    // =========================
    // INNER DTOs
    // =========================

    @Getter
    @Setter
    public static class PostMediaDTO {
        private Long id;
        private String url;
        private String type;
        private Integer displayOrder;
    }

    @Getter
    @Setter
    public static class PostPetDTO {
        private Long id;
        private String name;
        private String breedName;
        private String speciesName;
        private String avatarUrl;
        private Long ownerId;
        private String ownerUsername;
    }
}
