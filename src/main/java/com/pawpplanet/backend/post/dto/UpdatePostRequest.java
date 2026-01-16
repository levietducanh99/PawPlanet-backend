package com.pawpplanet.backend.post.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UpdatePostRequest {
    private String content;

    private String hashtags;

    private String type;

    private String contactInfo;

    private String location;

    private List<Long> petIds;
}
