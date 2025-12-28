package com.pawpplanet.backend.user.mapper;

import com.pawpplanet.backend.user.dto.UserProfileDTO;
import com.pawpplanet.backend.user.entity.UserEntity;

import java.time.ZoneId;

public class UserMapper {

    public static UserProfileDTO toUserProfileDTO(
            UserEntity user,
            long followersCount,
            long followingCount,
            long petsCount
    ) {
        UserProfileDTO dto = new UserProfileDTO();

        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setUsername(user.getUsername());
        dto.setRole(user.getRole());
        dto.setAvatarUrl(user.getAvatarUrl());
        dto.setBio(user.getBio());
        dto.setIsVerified(user.getIsVerified());
        dto.setCreatedAt(
                user.getCreatedAt()
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
        );

        dto.setFollowersCount(followersCount);
        dto.setFollowingCount(followingCount);
        dto.setPetsCount(petsCount);

        return dto;
    }
}
