package com.pawpplanet.backend.post.service;

import com.pawpplanet.backend.post.dto.LikeDetailResponse;
import com.pawpplanet.backend.post.dto.LikeRequest;
import com.pawpplanet.backend.post.dto.LikeResponse;

import java.util.List;

public interface LikeService {

    LikeResponse toggleLike(LikeRequest request);
    List<LikeDetailResponse> getLikesByPostId(Long postId);

}

