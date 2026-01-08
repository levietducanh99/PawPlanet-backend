package com.pawpplanet.backend.post.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MediaUrlRequest {
    private String publicId;  // Cloudinary public_id (e.g., "zrrwxpimqhmluja6khj2")
    private String type;      // image | video
}

