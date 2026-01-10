package com.pawpplanet.backend.search.service;

import com.pawpplanet.backend.search.dto.GlobalSearchResponse;

public interface GlobalSearchService {
    
    /**
     * Search for users and pets by keyword
     * 
     * @param keyword search query (minimum 2 characters)
     * @param types filter by entity types (comma-separated: "user", "pet", or null for both)
     * @param limit maximum results per type (default: 10)
     * @return GlobalSearchResponse containing users and/or pets
     */
    GlobalSearchResponse search(String keyword, String types, Integer limit);
}
