package com.pawpplanet.backend.media.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for generating Cloudinary upload signature.
 * Contains context information to determine the upload folder and naming rules.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaSignRequest {

    /**
     * Upload context - determines the folder structure and validation rules
     */
    @NotNull(message = "Context is required")
    private UploadContext context;

    /**
     * Owner ID - interpretation depends on context:
     * - USER_AVATAR: userId
     * - PET_AVATAR: petId (backend verifies user owns the pet)
     * - PET_GALLERY: petId (backend verifies user owns the pet)
     * - POST_MEDIA: postId
     */
    private Long ownerId;

    /**
     * Slug - required for ENCYCLOPEDIA contexts
     * Will be normalized to lowercase kebab-case format in the service layer
     * Used to construct folder paths like: pawplanet/encyclopedia/breeds/{slug}
     */
    private String slug;

    /**
     * Optional resource type override
     * Default: image
     * Valid values: image, video, raw, auto
     */
    private String resourceType;
}

