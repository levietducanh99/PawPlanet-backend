package com.pawpplanet.backend.utils;

import com.pawpplanet.backend.common.exception.AppException;
import com.pawpplanet.backend.common.exception.ErrorCode;
import com.pawpplanet.backend.user.entity.UserEntity;
import com.pawpplanet.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityHelper {

    private final UserRepository userRepository;


    /**
     * Get current authenticated user
     */
    public UserEntity getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new AppException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        String email = auth.getName();
        if (email == null || "anonymousUser".equals(email)) {
            throw new AppException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    /**
     * Get current authenticated user ID or null if not authenticated
     * Used for optional authentication scenarios (e.g., public endpoints that enhance with user data)
     */
    public Long getCurrentUserIdOrNull() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated()) {
                return null;
            }

            String email = auth.getName();
            if (email == null || "anonymousUser".equals(email)) {
                return null;
            }

            return userRepository.findByEmail(email)
                    .map(UserEntity::getId)
                    .orElse(null);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Check if current user is admin
     */
    public boolean isAdmin() {
        try {
            UserEntity user = getCurrentUser();
            return "ADMIN".equals(user.getRole());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Require admin access - throws exception if not admin
     */
    public void requireAdmin() {
        if (!isAdmin()) {
            throw new AppException(ErrorCode.ADMIN_ACCESS_REQUIRED);
        }
    }
}
