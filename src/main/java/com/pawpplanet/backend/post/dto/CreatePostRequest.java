package com.pawpplanet.backend.post.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreatePostRequest {
    private String content;

    private String hashtags;

    private String type;
    // normal | adoption | sale

    private String contactInfo;

    private String location;

    private List<Long> petIds;

    private List<MediaUrlRequest> mediaUrls;
}
