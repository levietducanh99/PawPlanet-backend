package com.pawpplanet.backend.pet.service.impl;

import com.pawpplanet.backend.common.exception.AppException;
import com.pawpplanet.backend.common.exception.ErrorCode;
import com.pawpplanet.backend.pet.dto.PetProfileDTO;
import com.pawpplanet.backend.pet.entity.FollowPetEntity;
import com.pawpplanet.backend.pet.entity.FollowPetId;
import com.pawpplanet.backend.pet.entity.PetMediaEntity;
import com.pawpplanet.backend.pet.mapper.PetMapper;
import com.pawpplanet.backend.pet.repository.FollowPetRepository;
import com.pawpplanet.backend.pet.repository.PetMediaRepository;
import com.pawpplanet.backend.pet.repository.PetRepository;
import com.pawpplanet.backend.pet.service.FollowPetService;
import com.pawpplanet.backend.user.dto.UserResponse;
import com.pawpplanet.backend.user.entity.UserEntity;
import com.pawpplanet.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class FollowPetServiceImpl implements FollowPetService {

    private final FollowPetRepository followPetRepository;
    private final PetRepository petRepository;
    private final UserRepository userRepository;
    private final PetMediaRepository petMediaRepository;

    @Override
    public void followPet(Long petId) {
        String currentEmail = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication().getName();
        UserEntity currentUser = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        petRepository.findById(petId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));

        FollowPetId id = new FollowPetId(currentUser.getId(), petId);
        boolean exists = followPetRepository.existsById(id);
        if (exists) return;

        FollowPetEntity rel = new FollowPetEntity();
        rel.setId(id);
        rel.setCreatedAt(Instant.now());
        followPetRepository.save(rel);
    }

    @Override
    public void unfollowPet(Long petId) {
        String currentEmail = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication().getName();
        UserEntity currentUser = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        FollowPetId id = new FollowPetId(currentUser.getId(), petId);
        if (!followPetRepository.existsById(id)) {
            throw new AppException(ErrorCode.FOLLOW_RELATIONSHIP_NOT_FOUND);
        }
        followPetRepository.deleteById(id);
    }

    @Override
    public boolean isFollowingPet(Long petId) {
        String currentEmail = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication().getName();
        UserEntity currentUser = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        FollowPetId id = new FollowPetId(currentUser.getId(), petId);
        return followPetRepository.existsById(id);
    }

    @Override
    public List<UserResponse> getFollowersByPet(Long petId) {
        petRepository.findById(petId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));

        List<FollowPetEntity> rels = followPetRepository.findByIdPetId(petId);
        return rels.stream()
                .map(r -> r.getId().getUserId())
                .map(userRepository::findById)
                .map(opt -> opt.orElse(null))
                .filter(u -> u != null)
                .map(this::toUserResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<PetProfileDTO> getFollowingPetsByUser(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        List<FollowPetEntity> rels = followPetRepository.findByIdUserId(userId);
        List<Long> petIds = rels.stream()
                .map(r -> r.getId().getPetId())
                .toList();

        return petRepository.findAllById(petIds).stream()
                .map(pet -> {
                    // 4. Lấy media cho từng pet (Cần petMediaRepository)
                    List<PetMediaEntity> mediaList = petMediaRepository.findByPetId(pet.getId());
                    return PetMapper.toProfileDTO(pet, mediaList);
                })
                .collect(Collectors.toList());
    }



    private UserResponse toUserResponse(UserEntity user) {
        UserResponse dto = new UserResponse();
        dto.setEmail(user.getEmail());
        dto.setUsername(user.getUsername());
        dto.setAvatarUrl(user.getAvatarUrl());
        dto.setBio(user.getBio());
        dto.setRole(user.getRole());
        return dto;
    }
}

