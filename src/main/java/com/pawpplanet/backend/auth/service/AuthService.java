package com.pawpplanet.backend.auth.service;

import com.pawpplanet.backend.auth.dto.LoginRequest;
import com.pawpplanet.backend.auth.dto.RegisterRequest;
import com.pawpplanet.backend.common.exception.AppException;
import com.pawpplanet.backend.common.exception.ErrorCode;
import com.pawpplanet.backend.user.entity.UserEntity;
import com.pawpplanet.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    public UserEntity register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USERNAME_ALREADY_EXISTS);
        }

        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(request.getEmail());
        userEntity.setUsername(request.getUsername());
        userEntity.setPassword(request.getPassword()); // In real application, password should be hashed
        userEntity.setBio(request.getBio());
        userEntity.setAvatarUrl(request.getAvatarUrl());
        return userRepository.save(userEntity);
    }

    public UserEntity login(LoginRequest request) {
        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        if (!user.getPassword().equals(request.getPassword())) {
            throw new AppException(ErrorCode.INVALID_PASSWORD);
        }
            return user;
    }
}
