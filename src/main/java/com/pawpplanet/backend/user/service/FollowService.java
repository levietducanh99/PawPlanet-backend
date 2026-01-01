package com.pawpplanet.backend.user.service;

import com.pawpplanet.backend.user.dto.UserResponse;

import java.util.List;

public interface FollowService {
    void follow(Long targetUserId);
    void unfollow(Long targetUserId);
    boolean isFollowing(Long targetUserId);

    // List users who follow the specified user
    List<UserResponse> getFollowers(Long userId);

    // List users whom the specified user is following
    List<UserResponse> getFollowing(Long userId);
}
