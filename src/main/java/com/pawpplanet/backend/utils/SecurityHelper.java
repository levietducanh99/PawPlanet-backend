package com.pawpplanet.backend.utils;

import com.pawpplanet.backend.common.exception.AppException;
import com.pawpplanet.backend.common.exception.ErrorCode;
import com.pawpplanet.backend.user.entity.UserEntity;
import com.pawpplanet.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityHelper {

    private final UserRepository userRepository;

    /**
     * Get current authenticated user ID from JWT token (FAST - no DB query)
     * This method extracts userId directly from JWT claims.
     *
     * @return User ID from token
     * @throws AppException if not authenticated or userId not found in token
     */
    public Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new AppException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        // Get userId from JWT token claims
        if (auth.getPrincipal() instanceof Jwt) {
            Jwt jwt = (Jwt) auth.getPrincipal();
            Object userIdClaim = jwt.getClaim("userId");

            if (userIdClaim != null) {
                // Handle both Integer and Long
                if (userIdClaim instanceof Integer) {
                    return ((Integer) userIdClaim).longValue();
                } else if (userIdClaim instanceof Long) {
                    return (Long) userIdClaim;
                }
            }
        }

        // Fallback: query from database if userId not in token (backward compatibility)
        String email = auth.getName();
        if (email == null || "anonymousUser".equals(email)) {
            throw new AppException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        return userRepository.findByEmail(email)
                .map(UserEntity::getId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    /**
     * Get current authenticated user ID from JWT token or null if not authenticated (FAST)
     * Used for optional authentication scenarios.
     *
     * @return User ID from token or null
     */
    public Long getCurrentUserIdFromTokenOrNull() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated()) {
                return null;
            }

            // Get userId from JWT token claims
            if (auth.getPrincipal() instanceof Jwt) {
                Jwt jwt = (Jwt) auth.getPrincipal();
                Object userIdClaim = jwt.getClaim("userId");

                if (userIdClaim != null) {
                    if (userIdClaim instanceof Integer) {
                        return ((Integer) userIdClaim).longValue();
                    } else if (userIdClaim instanceof Long) {
                        return (Long) userIdClaim;
                    }
                }
            }

            // Fallback: query from database
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
     *
     * @deprecated Use getCurrentUserIdFromTokenOrNull() for better performance
     */
    @Deprecated
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
