package com.pawpplanet.backend.post.service;

import com.pawpplanet.backend.post.dto.CommentDetailResponse;
import com.pawpplanet.backend.post.dto.CommentRequest;
import com.pawpplanet.backend.post.dto.CommentResponse;

import java.util.List;

public interface CommentService {
    CommentResponse createComment(CommentRequest request);
    List<CommentDetailResponse> getCommentsByPostId(Long postId);
}
