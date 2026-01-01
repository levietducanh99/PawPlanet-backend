package com.pawpplanet.backend.pet.service;

import com.pawpplanet.backend.pet.dto.PetProfileDTO;
import com.pawpplanet.backend.user.dto.UserResponse;

import java.util.List;

public interface FollowPetService {
    void followPet(Long petId);
    void unfollowPet(Long petId);
    boolean isFollowingPet(Long petId);

    // List users who follow a pet
    List<UserResponse> getFollowersByPet(Long petId);

    // List pets followed by a user
    List<PetProfileDTO> getFollowingPetsByUser(Long userId);
}
