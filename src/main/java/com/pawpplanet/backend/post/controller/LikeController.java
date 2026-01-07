package com.pawpplanet.backend.post.controller;

import com.pawpplanet.backend.post.dto.LikeDetailResponse;
import com.pawpplanet.backend.post.dto.LikeRequest;
import com.pawpplanet.backend.post.dto.LikeResponse;
import com.pawpplanet.backend.post.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/likes")
public class LikeController {

    private final LikeService likeService;

    @PostMapping()
    public LikeResponse toggleLike(@RequestBody LikeRequest request) {
        return likeService.toggleLike(request);
    }
    @GetMapping("/{postId}")
    public ResponseEntity<List<LikeDetailResponse>> getAllLikes(@PathVariable Long postId) {
        return ResponseEntity.ok(likeService.getLikesByPostId(postId));
    }
}

