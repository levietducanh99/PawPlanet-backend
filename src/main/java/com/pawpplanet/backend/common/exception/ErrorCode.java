package com.pawpplanet.backend.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    INTERNAL_SERVER_ERROR(500, "Internal server error"),
    INVALID_CREDENTIALS(401, "Invalid credentials"),
    INVALID_REQUEST(400, "Invalid request"),
    INVALID_GOOGLE_TOKEN(401, "Invalid or expired Google token"),

    SAME_PASSWORD(400, "New password cannot be the same as the old password"),

    // Media upload errors
    INVALID_UPLOAD_CONTEXT(400, "Invalid upload context"),
    MISSING_OWNER_ID(400, "Owner ID is required for this upload context"),
    MISSING_SLUG(400, "Slug is required for this upload context"),
    INVALID_SLUG_FORMAT(400, "Slug must be lowercase kebab-case (e.g., 'golden-retriever')"),
    SLUG_NOT_FOUND(404, "The specified slug does not exist in the database"),
    MEDIA_SIGNATURE_GENERATION_FAILED(500, "Failed to generate upload signature"),
    PET_NOT_FOUND(404, "Pet not found"),
    UNAUTHORIZED_PET_ACCESS(403, "You do not have permission to upload media for this pet"),

    // Admin and Encyclopedia errors
    ADMIN_ACCESS_REQUIRED(403, "Admin access required for this operation"),
    SPECIES_NOT_FOUND(404, "Species not found"),
    BREED_NOT_FOUND(404, "Breed not found"),
    ANIMAL_CLASS_NOT_FOUND(404, "Animal class not found"),
    INVALID_MEDIA_ROLE(400, "Invalid media role. Must be 'hero', 'gallery', or 'avatar'"),
    INVALID_MEDIA_TYPE(400, "Invalid media type. Must be 'image' or 'video'"),
    MEDIA_NOT_FOUND(404, "Media not found"),
    ENCYCLOPEDIA_MEDIA_NOT_FOUND(404, "Encyclopedia media not found"),
    UNAUTHORIZED_MEDIA_DELETE(403, "You do not have permission to delete this media")
    ;
    private int code;
    private String message;
}
