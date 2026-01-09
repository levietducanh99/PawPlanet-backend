package com.pawpplanet.backend.user.service.impl;

import com.pawpplanet.backend.common.exception.AppException;
import com.pawpplanet.backend.common.exception.ErrorCode;
import com.pawpplanet.backend.notification.helper.NotificationHelper;
import com.pawpplanet.backend.user.dto.UserResponse;
import com.pawpplanet.backend.user.entity.FollowUser;
import com.pawpplanet.backend.user.entity.FollowUserId;
import com.pawpplanet.backend.user.entity.UserEntity;
import com.pawpplanet.backend.user.repository.FollowUserRepository;
import com.pawpplanet.backend.user.repository.UserRepository;
import com.pawpplanet.backend.user.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class FollowServiceImpl implements FollowService {

    private final UserRepository userRepository;
    private final FollowUserRepository followUserRepository;
    private final NotificationHelper notificationHelper;

    @Override
    public void follow(Long targetUserId) {
        // get current user from security context
        String currentEmail = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication().getName();
        UserEntity currentUser = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (currentUser.getId().equals(targetUserId)) {
            throw new AppException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        UserEntity target = userRepository.findById(targetUserId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        FollowUserId id = new FollowUserId(currentUser.getId(), target.getId());
        boolean exists = followUserRepository.existsById(id);
        if (exists) return; // already following, idempotent

        FollowUser rel = new FollowUser();
        rel.setId(id);
        rel.setFollower(currentUser);
        rel.setFollowing(target);
        rel.setCreatedAt(LocalDateTime.now());

        followUserRepository.save(rel);

        // Send notification to the user being followed
        notificationHelper.notifyFollowUser(targetUserId, currentUser);
    }

    @Override
    public void unfollow(Long targetUserId) {
        String currentEmail = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication().getName();
        UserEntity currentUser = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        FollowUserId id = new FollowUserId(currentUser.getId(), targetUserId);
        if (!followUserRepository.existsById(id)) {
            throw new AppException(ErrorCode.FOLLOW_RELATIONSHIP_NOT_FOUND);
        }
        followUserRepository.deleteById(id);
    }

    @Override
    public boolean isFollowing(Long targetUserId) {
        String currentEmail = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication().getName();
        UserEntity currentUser = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        FollowUserId id = new FollowUserId(currentUser.getId(), targetUserId);
        return followUserRepository.existsById(id);
    }

    @Override
    public List<UserResponse> getFollowers(Long userId) {
        // verify user exists
        UserEntity target = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        List<FollowUser> relations = followUserRepository.findByIdFollowingId(userId);
        return relations.stream()
                .map(FollowUser::getFollower)
                .map(this::toUserResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserResponse> getFollowing(Long userId) {
        // verify user exists
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        List<FollowUser> relations = followUserRepository.findByIdFollowerId(userId);
        return relations.stream()
                .map(FollowUser::getFollowing)
                .map(this::toUserResponse)
                .collect(Collectors.toList());
    }

    private UserResponse toUserResponse(UserEntity user) {
        UserResponse dto = new UserResponse();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setUsername(user.getUsername());
        dto.setAvatarUrl(user.getAvatarUrl());
        dto.setBio(user.getBio());
        dto.setRole(user.getRole());
        return dto;
    }
}
