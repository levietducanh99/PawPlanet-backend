package com.pawpplanet.backend.post.controller;

import com.pawpplanet.backend.post.dto.CommentDetailResponse;
import com.pawpplanet.backend.post.dto.CommentRequest;
import com.pawpplanet.backend.post.dto.CommentResponse;
import com.pawpplanet.backend.post.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/comments")
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public CommentResponse createComment(@RequestBody @Valid CommentRequest request) {
        return commentService.createComment(request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{postId}")
    public ResponseEntity<List<CommentDetailResponse>> getAllComments(@PathVariable Long postId) {
        return ResponseEntity.ok(commentService.getCommentsByPostId(postId));
    }
}
