package com.pawpplanet.backend.explore.service;

import com.pawpplanet.backend.explore.dto.ExploreResponse;

public interface ExploreService {
    /**
     * Get explore feed with random posts, pets, and users
     *
     * @param limit Total number of items to return (default: 30, max: 50)
     * @param seed Random seed for consistent pagination (optional)
     * @param include Comma-separated list of entity types: post,pet,user (default: all)
     * @return ExploreResponse with randomized items and seed for pagination
     */
    ExploreResponse getExploreFeed(Integer limit, String seed, String include);
}

