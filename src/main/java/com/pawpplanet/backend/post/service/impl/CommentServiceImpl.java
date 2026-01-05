package com.pawpplanet.backend.post.service.impl;

import com.pawpplanet.backend.notification.service.NotificationService;
import com.pawpplanet.backend.post.dto.CommentDetailResponse;
import com.pawpplanet.backend.post.dto.CommentRequest;
import com.pawpplanet.backend.post.dto.CommentResponse;
import com.pawpplanet.backend.post.entity.CommentEntity;
import com.pawpplanet.backend.post.entity.PostEntity;
import com.pawpplanet.backend.post.repository.CommentRepository;
import com.pawpplanet.backend.post.repository.PostRepository;
import com.pawpplanet.backend.post.service.CommentService;
import com.pawpplanet.backend.user.entity.UserEntity;
import com.pawpplanet.backend.user.repository.UserRepository;
import com.pawpplanet.backend.utils.SecurityHelper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final NotificationService notificationService;
    private final SecurityHelper securityHelper;
    private final UserRepository userRepository;

    @Override
    public CommentResponse createComment(CommentRequest request) {

        Long userId = securityHelper.getCurrentUser().getId();
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        PostEntity post = postRepository.findById(request.getPostId())
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found")
                );

        CommentEntity comment = new CommentEntity();
        comment.setPostId(post.getId());
        comment.setUserId(userId);
        comment.setContent(request.getContent());
        comment.setParentId(request.getParentId());

        commentRepository.save(comment);

        // tạo notification nếu không phải chủ bài viết
        if (!post.getAuthorId().equals(userId)) {
            notificationService.createNotification(
                    post.getAuthorId(),
                    "COMMENT",
                    post.getId()
            );
        }

        return mapToResponse(comment);
    }

    private CommentResponse mapToResponse(CommentEntity entity) {
        CommentResponse res = new CommentResponse();
        res.setId(entity.getId());
        res.setPostId(entity.getPostId());
        res.setUserId(entity.getUserId());
        res.setContent(entity.getContent());
        res.setCreatedAt(entity.getCreatedAt());
        return res;
    }
    @Override
    public List<CommentDetailResponse> getCommentsByPostId(Long postId) {

        List<CommentEntity> allComments = commentRepository.findByPostId(postId);

        return allComments.stream()
                // CHỈ LẤY COMMENT KHÔNG PHẢI LÀ CON
                .filter(c -> c.getParentId() == null)
                .map(c -> mapToDetailResponse(c, allComments))
                .collect(Collectors.toList());
    }

    private CommentDetailResponse mapToDetailResponse(CommentEntity entity, List<CommentEntity> allComments) {
        CommentDetailResponse res = new CommentDetailResponse();
        res.setId(entity.getId());
        res.setUserId(entity.getUserId());
        res.setContent(entity.getContent());
        res.setCreatedAt(entity.getCreatedAt());

        UserEntity user = userRepository.findById(entity.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));


        res.setUserName(user.getUsername());
        res.setUserAvatar(user.getAvatarUrl());

        List<CommentDetailResponse> replies = allComments.stream()
                .filter(c -> entity.getId().equals(c.getParentId()))
                .map(c -> mapToDetailResponse(c, allComments))
                .collect(Collectors.toList());

        res.setReplies(replies);
        return res;
    }
}


