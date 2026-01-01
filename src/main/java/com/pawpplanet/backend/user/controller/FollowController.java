package com.pawpplanet.backend.user.controller;

import com.pawpplanet.backend.common.dto.ApiResponse;
import com.pawpplanet.backend.user.dto.UserResponse;
import com.pawpplanet.backend.user.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    @PostMapping("/{id}/follow")
    public ApiResponse<Void> follow(@PathVariable("id") Long id) {
        ApiResponse<Void> response = new ApiResponse<>();
        followService.follow(id);
        response.setMessage("Follow user successfully");
        response.setStatusCode(200);
        return response;
    }

    @DeleteMapping("/{id}/follow")
    public ApiResponse<Void> unfollow(@PathVariable("id") Long id) {
        ApiResponse<Void> response = new ApiResponse<>();
        followService.unfollow(id);
        response.setMessage("Unfollow user sucessfully");
        response.setStatusCode(200);
        return response;
    }

    @GetMapping("/{id}/follow/status")
    public ApiResponse<Boolean> isFollowing(@PathVariable("id") Long id) {
        ApiResponse<Boolean> response = new ApiResponse<>();
        boolean status = followService.isFollowing(id);
        response.setResult(status);
        response.setMessage("OK");
        response.setStatusCode(200);
        return response;
    }

    @GetMapping("/{id}/followers")
    public ApiResponse<List<UserResponse>> getFollowers(@PathVariable("id") Long id) {
        ApiResponse<List<UserResponse>> response = new ApiResponse<>();
        List<UserResponse> users = followService.getFollowers(id);
        response.setResult(users);
        response.setMessage("OK");
        response.setStatusCode(200);
        return response;
    }

    @GetMapping("/{id}/following")
    public ApiResponse<List<UserResponse>> getFollowing(@PathVariable("id") Long id) {
        ApiResponse<List<UserResponse>> response = new ApiResponse<>();
        List<UserResponse> users = followService.getFollowing(id);
        response.setResult(users);
        response.setMessage("OK");
        response.setStatusCode(200);
        return response;
    }
}
