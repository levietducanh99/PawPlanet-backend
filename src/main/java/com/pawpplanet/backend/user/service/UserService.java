package com.pawpplanet.backend.user.service;

import com.pawpplanet.backend.user.dto.UpdateProfileRequestDTO;
import com.pawpplanet.backend.user.dto.UserProfileDTO;

public interface UserService {
    UserProfileDTO viewProfile();
    UserProfileDTO updateMyInformation(UpdateProfileRequestDTO request);
    UserProfileDTO getUserProfileById(Long userId);

}
