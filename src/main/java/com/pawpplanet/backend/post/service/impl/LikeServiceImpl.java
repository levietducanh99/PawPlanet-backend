package com.pawpplanet.backend.post.service.impl;

import com.pawpplanet.backend.notification.helper.NotificationHelper;
import com.pawpplanet.backend.post.dto.LikeDetailResponse;
import com.pawpplanet.backend.post.dto.LikeRequest;
import com.pawpplanet.backend.post.dto.LikeResponse;
import com.pawpplanet.backend.post.entity.LikeEntity;
import com.pawpplanet.backend.post.entity.PostEntity;
import com.pawpplanet.backend.post.repository.LikeRepository;
import com.pawpplanet.backend.post.repository.PostRepository;
import com.pawpplanet.backend.post.service.LikeService;
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
public class LikeServiceImpl implements LikeService {

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final NotificationHelper notificationHelper;
    private final SecurityHelper securityHelper;
    private final UserRepository userRepository;

    @Override
    public LikeResponse toggleLike(LikeRequest request) {

        UserEntity currentUser = securityHelper.getCurrentUser();
        Long userId = currentUser.getId();

        PostEntity post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));

        boolean exists = likeRepository.existsByPostIdAndUserId(post.getId(), userId);

        boolean liked;
        if (exists) {
            // unlike
            likeRepository.deleteByPostIdAndUserId(post.getId(), userId);
            liked = false;
        } else {
            // like
            likeRepository.save(new LikeEntity(
                    userId,
                    post.getId(),
                    LocalDateTime.now()
            ));
            liked = true;

            // Send notification to post author (if not self-like)
            if (!post.getAuthorId().equals(userId)) {
                notificationHelper.notifyLikePost(post.getAuthorId(), currentUser, post);
            }
        }

        return new LikeResponse(
                post.getId(),
                liked,
                likeRepository.countByPostId(post.getId())
        );
    }

    @Override
    public List<LikeDetailResponse> getLikesByPostId(Long postId) {
        List<LikeEntity> likes = likeRepository.findByPostId(postId);

        return likes.stream()
                .map(like -> {
                    UserEntity user = userRepository.findById(like.getUserId())
                            .orElseThrow(() -> new RuntimeException("User not found"));

                    return new LikeDetailResponse(
                            user.getId(),
                            user.getUsername(),
                            user.getAvatarUrl()
                    );
                })
                .collect(Collectors.toList());
    }
}



