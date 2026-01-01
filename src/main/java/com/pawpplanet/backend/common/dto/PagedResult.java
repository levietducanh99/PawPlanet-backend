package com.pawpplanet.backend.common.dto;

import lombok.Data;

import java.util.List;

@Data
public class PagedResult<T> {
    private List<T> items;
    private long totalElements;
    private int page;
    private int size;
}

