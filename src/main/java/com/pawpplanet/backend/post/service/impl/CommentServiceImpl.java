package com.pawpplanet.backend.post.service.impl;

import com.pawpplanet.backend.notification.service.NotificationService;
import com.pawpplanet.backend.post.dto.CommentRequest;
import com.pawpplanet.backend.post.dto.CommentResponse;
import com.pawpplanet.backend.post.entity.CommentEntity;
import com.pawpplanet.backend.post.entity.PostEntity;
import com.pawpplanet.backend.post.repository.CommentRepository;
import com.pawpplanet.backend.post.repository.PostRepository;
import com.pawpplanet.backend.post.service.CommentService;
import com.pawpplanet.backend.utils.SecurityHelper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final NotificationService notificationService;
    private final SecurityHelper securityHelper;

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
}


