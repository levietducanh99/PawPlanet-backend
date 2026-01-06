package com.pawpplanet.backend.encyclopedia.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class AddEncyclopediaMediaRequest {

    @NotEmpty(message = "Media items cannot be empty")
    @Valid
    private List<MediaItem> mediaItems;

    @Data
    public static class MediaItem {
        @NotNull(message = "Type is required")
        private String type; // "image" or "video"

        @NotNull(message = "URL is required")
        private String url;

        @NotNull(message = "Role is required")
        private String role; // "hero", "gallery", or "thumbnail"
    }
}

