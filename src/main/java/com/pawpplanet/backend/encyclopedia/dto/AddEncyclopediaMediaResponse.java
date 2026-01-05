package com.pawpplanet.backend.encyclopedia.dto;

import lombok.Data;

import java.util.List;

@Data
public class AddEncyclopediaMediaResponse {
    private List<EncyclopediaMediaResponse> addedMedia;
    private int totalCount;
    private String message;
}

