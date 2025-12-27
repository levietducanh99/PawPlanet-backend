package com.pawpplanet.backend.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum ErrorCode {
    USER_NOT_FOUND(404, "User not found"),
    INVALID_PASSWORD(401, "Invalid password"),
    EMAIL_ALREADY_EXISTS(409, "Email already exists"),
    USERNAME_ALREADY_EXISTS(409, "Username already exists"),
    INVALID_TOKEN(401, "Invalid token"),
    POST_NOT_FOUND(404, "Post not found"),
    UNAUTHORIZED_ACCESS(403, "Unauthorized access"),
    FOLLOW_RELATIONSHIP_NOT_FOUND(404, "Follow relationship not found"),
    INTERNAL_SERVER_ERROR(500, "Internal server error")
    ;
    private int code;
    private String message;
}
