package com.pawpplanet.backend.explore.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Lightweight post DTO for explore feed - only essential fields
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostExploreDTO {
    private Long id;
    private String content;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    // Author info (inline for performance)
    private Long authorId;
    private String authorUsername;
    private String authorAvatarUrl;

    // Interaction counts
    private Integer likeCount;
    private Integer commentCount;
    private Boolean liked;

    // Media (first image only for explore preview)
    private List<MediaDTO> media;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MediaDTO {
        private String url;
        private String type;
    }
}

