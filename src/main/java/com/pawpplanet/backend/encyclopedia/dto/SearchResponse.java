package com.pawpplanet.backend.encyclopedia.dto;

import lombok.Data;

import java.util.List;

@Data
public class SearchResponse {
    private List<SearchResultItem> items;
}

