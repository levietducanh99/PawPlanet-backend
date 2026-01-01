package com.pawpplanet.backend.post.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MediaUrlRequest {
    private String url;
    private String type; // image | video
}

