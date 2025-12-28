package com.pawpplanet.backend.user.service.impl;

import com.pawpplanet.backend.pet.repository.PetRepository;
import com.pawpplanet.backend.user.dto.UpdateProfileRequestDTO;
import com.pawpplanet.backend.user.dto.UserProfileDTO;
import com.pawpplanet.backend.user.entity.UserEntity;
import com.pawpplanet.backend.user.mapper.UserMapper;
import com.pawpplanet.backend.user.repository.FollowUserRepository;
import com.pawpplanet.backend.user.repository.UserRepository;
import com.pawpplanet.backend.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final FollowUserRepository followUserRepository;
    private final PetRepository petRepository;

    public UserServiceImpl(
            UserRepository userRepository,
            FollowUserRepository followUserRepository,
            PetRepository petRepository
    ) {
        this.userRepository = userRepository;
        this.followUserRepository = followUserRepository;
        this.petRepository = petRepository;
    }

    @Override
    public UserProfileDTO viewProfile() {
        String email = "user1@example.com"; // TODO: lấy từ JWT

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        return buildUserProfileDTO(user);
    }

    @Override
    public UserProfileDTO updateMyInformation(UpdateProfileRequestDTO request) {
        String email = "user1@example.com"; // TODO: JWT

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (request.getAvatarUrl() != null) {
            user.setAvatarUrl(request.getAvatarUrl());
        }
        if (request.getBio() != null) {
            user.setBio(request.getBio());
        }

        UserEntity saved = userRepository.save(user);
        return buildUserProfileDTO(saved);
    }

    private UserProfileDTO buildUserProfileDTO(UserEntity user) {
        long followers = followUserRepository.countByIdFollowingId(user.getId());
        long following = followUserRepository.countByIdFollowerId(user.getId());
        long pets = petRepository.countByOwnerId(user.getId());

        return UserMapper.toUserProfileDTO(
                user,
                followers,
                following,
                pets
        );
    }
}
