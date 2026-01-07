package com.pawpplanet.backend.utils;

import com.pawpplanet.backend.common.exception.AppException;
import com.pawpplanet.backend.common.exception.ErrorCode;
import com.pawpplanet.backend.user.entity.UserEntity;
import com.pawpplanet.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
@RequiredArgsConstructor
public class SecurityHelper {

    private final UserRepository userRepository;

    public UserEntity getCurrentUser() {
        var auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Chưa đăng nhập"
            );
        }

        return userRepository.findByEmail(auth.getName())
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Không tìm thấy user"
                        ));
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
