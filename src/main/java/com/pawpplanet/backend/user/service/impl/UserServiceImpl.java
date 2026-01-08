package com.pawpplanet.backend.user.service.impl;

import com.pawpplanet.backend.media.service.CloudinaryUrlBuilder;
import com.pawpplanet.backend.pet.repository.PetRepository;
import com.pawpplanet.backend.user.dto.UpdateProfileRequestDTO;
import com.pawpplanet.backend.user.dto.UserProfileDTO;
import com.pawpplanet.backend.user.entity.UserEntity;
import com.pawpplanet.backend.user.mapper.UserMapper;
import com.pawpplanet.backend.user.repository.FollowUserRepository;
import com.pawpplanet.backend.user.repository.UserRepository;
import com.pawpplanet.backend.user.service.UserService;
import com.pawpplanet.backend.utils.SecurityHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final FollowUserRepository followUserRepository;
    private final PetRepository petRepository;
    private final CloudinaryUrlBuilder cloudinaryUrlBuilder;
    private final SecurityHelper securityHelper;



    @Override
    public UserProfileDTO viewProfile() {
//        String email = "user1@example.com"; // TODO: lấy từ JWT
        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        return buildUserProfileDTO(user);
    }

    @Override
    public UserProfileDTO updateMyInformation(UpdateProfileRequestDTO request) {
        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // Update full name
        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }

        // Update avatar with publicId (builder validates internally)
        if (request.getAvatarPublicId() != null && !request.getAvatarPublicId().isBlank()) {
            user.setAvatarPublicId(request.getAvatarPublicId());
            user.setAvatarUrl(cloudinaryUrlBuilder.buildOptimizedUrl(
                    request.getAvatarPublicId(),
                    "image"
            ));
        }

        // Update cover image with publicId (builder validates internally)
        if (request.getCoverImagePublicId() != null && !request.getCoverImagePublicId().isBlank()) {
            user.setCoverImagePublicId(request.getCoverImagePublicId());
            user.setCoverImageUrl(cloudinaryUrlBuilder.buildOptimizedUrl(
                    request.getCoverImagePublicId(),
                    "image"
            ));
        }

        // Update bio
        if (request.getBio() != null) {
            user.setBio(request.getBio());
        }

        UserEntity saved = userRepository.save(user);
        return buildUserProfileDTO(saved);
    }
    @Override
    public UserProfileDTO getUserProfileById(Long userId) {

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "User not found"
                        ));

        return buildUserProfileDTO(user);
    }


    private UserProfileDTO buildUserProfileDTO(UserEntity user) {
        long followers = followUserRepository.countByIdFollowingId(user.getId());
        long following = followUserRepository.countByIdFollowerId(user.getId());
        long pets = petRepository.countByOwnerId(user.getId());

        UserProfileDTO dto = UserMapper.toUserProfileDTO(
                user,
                followers,
                following,
                pets
        );

        // Add computed fields for follow relationships
        Long currentUserId = securityHelper.getCurrentUser().getId();
        if (currentUserId != null) {
            boolean isMe = currentUserId.equals(user.getId());
            dto.setIsMe(isMe);

            if (isMe) {
                // Viewing own profile - skip all checks
                dto.setIsFollowing(false);
                dto.setIsFollowedBy(false);
                dto.setCanFollow(false);
            } else {
                // Optimized: Use custom query instead of creating composite key objects
                boolean isFollowing = followUserRepository.existsFollow(currentUserId, user.getId());
                boolean isFollowedBy = followUserRepository.existsFollow(user.getId(), currentUserId);

                dto.setIsFollowing(isFollowing);
                dto.setIsFollowedBy(isFollowedBy);
                // canFollow should depend ONLY on whether this is the current user's profile
                dto.setCanFollow(!isMe);
            }
        } else {
            // Not authenticated
            dto.setIsMe(false);
            dto.setIsFollowing(false);
            dto.setIsFollowedBy(false);
            dto.setCanFollow(false);
        }

        return dto;
    }

}
