package com.pawpplanet.backend.post.service.impl;

import com.pawpplanet.backend.notification.helper.NotificationHelper;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final NotificationHelper notificationHelper;
    private final SecurityHelper securityHelper;
    private final UserRepository userRepository;

    @Override
    public CommentResponse createComment(CommentRequest request) {

        UserEntity currentUser = securityHelper.getCurrentUser();
        Long userId = currentUser.getId();

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

        // Send notification to post author (if not commenting on own post)
        if (!post.getAuthorId().equals(userId)) {
            notificationHelper.notifyCommentPost(post.getAuthorId(), currentUser, comment, post);
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
    public void deleteComment(Long commentId) {
        UserEntity currentUser = securityHelper.getCurrentUser();

        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy comment")
                );

        if (!comment.getUserId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Không có quyền xóa comment này");
        }

        comment.setIsDeleted(true);
        comment.setDeletedAt(LocalDateTime.now());
        comment.setDeletedBy(currentUser.getId());

        commentRepository.save(comment);
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


