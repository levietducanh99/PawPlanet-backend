package com.pawpplanet.backend.pet.controller;

import com.pawpplanet.backend.common.dto.ApiResponse;
import com.pawpplanet.backend.pet.dto.PetProfileDTO;
import com.pawpplanet.backend.pet.service.FollowPetService;
import com.pawpplanet.backend.user.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pets")
@RequiredArgsConstructor
public class PetFollowController {

    private final FollowPetService followPetService;

    @PostMapping("/{id}/follow")
    public ApiResponse<Void> follow(@PathVariable("id") Long id) {
        ApiResponse<Void> response = new ApiResponse<>();
        followPetService.followPet(id);
        response.setMessage("Follow pet successfully");
        response.setStatusCode(200);
        return response;
    }

    @DeleteMapping("/{id}/follow")
    public ApiResponse<Void> unfollow(@PathVariable("id") Long id) {
        ApiResponse<Void> response = new ApiResponse<>();
        followPetService.unfollowPet(id);
        response.setMessage("Unfollow pet successfully");
        response.setStatusCode(200);
        return response;
    }

    @GetMapping("/{id}/follow/status")
    public ApiResponse<Boolean> isFollowing(@PathVariable("id") Long id) {
        ApiResponse<Boolean> response = new ApiResponse<>();
        boolean status = followPetService.isFollowingPet(id);
        response.setResult(status);
        response.setMessage("OK");
        response.setStatusCode(200);
        return response;
    }

    @GetMapping("/{id}/followers")
    public ApiResponse<List<UserResponse>> getFollowers(@PathVariable("id") Long id) {
        ApiResponse<List<UserResponse>> response = new ApiResponse<>();
        List<UserResponse> users = followPetService.getFollowersByPet(id);
        response.setResult(users);
        response.setMessage("OK");
        response.setStatusCode(200);
        return response;
    }

    @GetMapping("/users/{userId}/following")
    public ApiResponse<List<PetProfileDTO>> getFollowingPets(@PathVariable("userId") Long userId) {
        ApiResponse<List<PetProfileDTO>> response = new ApiResponse<>();
        List<PetProfileDTO> pets = followPetService.getFollowingPetsByUser(userId);
        response.setResult(pets);
        response.setMessage("OK");
        response.setStatusCode(200);
        return response;
    }
}

