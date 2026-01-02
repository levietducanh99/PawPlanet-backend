package com.pawpplanet.backend.media.service.impl;

import com.cloudinary.Cloudinary;
import com.pawpplanet.backend.common.exception.AppException;
import com.pawpplanet.backend.common.exception.ErrorCode;
import com.pawpplanet.backend.media.dto.MediaSignRequest;
import com.pawpplanet.backend.media.dto.MediaSignResponse;
import com.pawpplanet.backend.media.dto.UploadContext;
import com.pawpplanet.backend.media.service.MediaService;
import com.pawpplanet.backend.pet.entity.PetEntity;
import com.pawpplanet.backend.pet.repository.PetRepository;
import com.pawpplanet.backend.user.entity.UserEntity;
import com.pawpplanet.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class MediaServiceImpl implements MediaService {

    private final Cloudinary cloudinary;
    private final PetRepository petRepository;
    private final UserRepository userRepository;

    /**
     * Legacy method - kept for backward compatibility
     */
    @Override
    @Deprecated
    public Map<String, Object> getUploadSignature() {
        long timestamp = System.currentTimeMillis() / 1000;

        Map<String, Object> params = new HashMap<>();
        params.put("timestamp", timestamp);

        String apiSecret = (String) cloudinary.config.apiSecret;
        String signature = cloudinary.apiSignRequest(params, apiSecret);

        Map<String, Object> response = new HashMap<>();
        response.put("apiKey", cloudinary.config.apiKey);
        response.put("cloudName", cloudinary.config.cloudName);
        response.put("timestamp", timestamp);
        response.put("signature", signature);

        return response;
    }

    @Override
    public MediaSignResponse generateUploadSignature(MediaSignRequest request) {
        log.info("Generating upload signature for context: {}", request.getContext());

        // Validate request
        validateRequest(request);

        // Authorize pet ownership for PET contexts
        if (isPetContext(request.getContext())) {
            verifyPetOwnership(request.getOwnerId());
        }

        // Determine folder and public ID based on context
        String assetFolder = determineAssetFolder(request);
        String publicId = determinePublicId(request);
        String resourceType = request.getResourceType() != null ? request.getResourceType() : "image";

        // Generate timestamp
        long timestamp = System.currentTimeMillis() / 1000;

        // Build parameters for signature
        // NOTE: resource_type is NOT included in signature as per Cloudinary docs
        // Parameters to exclude from signature: file, cloud_name, resource_type, api_key
        Map<String, Object> params = new HashMap<>();
        params.put("timestamp", timestamp);
        params.put("asset_folder", assetFolder);

        if (publicId != null && !publicId.isEmpty()) {
            params.put("public_id", publicId);
        }

        // Generate signature
        String signature = generateSignature(params);

        // Build and return response
        MediaSignResponse response = MediaSignResponse.builder()
                .signature(signature)
                .timestamp(timestamp)
                .apiKey((String) cloudinary.config.apiKey)
                .cloudName((String) cloudinary.config.cloudName)
                .assetFolder(assetFolder)
                .publicId(publicId)
                .resourceType(resourceType)
                .build();

        log.info("Successfully generated upload signature for folder: {}", assetFolder);
        return response;
    }

    /**
     * Validate the request based on context requirements
     */
    private void validateRequest(MediaSignRequest request) {
        if (request.getContext() == null) {
            throw new AppException(ErrorCode.INVALID_UPLOAD_CONTEXT);
        }

        UploadContext context = request.getContext();

        // Validate ownerId for USER/PET/POST contexts
        if (requiresOwnerId(context)) {
            if (request.getOwnerId() == null) {
                throw new AppException(ErrorCode.MISSING_OWNER_ID);
            }
            if (request.getOwnerId() <= 0) {
                throw new AppException(ErrorCode.MISSING_OWNER_ID);
            }
        }

        // Validate slug for ENCYCLOPEDIA contexts
        if (requiresSlug(context)) {
            if (request.getSlug() == null || request.getSlug().trim().isEmpty()) {
                throw new AppException(ErrorCode.MISSING_SLUG);
            }
            // Normalize slug: lowercase, replace spaces with hyphens, remove invalid characters
            String slug = request.getSlug()
                    .trim()
                    .toLowerCase()
                    .replaceAll("\\s+", "-")  // Replace whitespace with hyphens
                    .replaceAll("[^a-z0-9-]", "")  // Remove invalid characters
                    .replaceAll("-+", "-")  // Replace multiple hyphens with single hyphen
                    .replaceAll("^-|-$", "");  // Remove leading/trailing hyphens

            if (slug.isEmpty() || !slug.matches("^[a-z0-9]+(-[a-z0-9]+)*$")) {
                throw new AppException(ErrorCode.INVALID_SLUG_FORMAT);
            }
            // Update the slug to normalized version
            request.setSlug(slug);
        }
    }

    /**
     * Determine the Cloudinary asset_folder based on context
     */
    private String determineAssetFolder(MediaSignRequest request) {
        UploadContext context = request.getContext();

        switch (context) {
            case USER_AVATAR:
                return String.format("pawplanet/users/%d/avatar", request.getOwnerId());

            case PET_AVATAR:
                return String.format("pawplanet/pets/%d/avatar", request.getOwnerId());

            case PET_GALLERY:
                return String.format("pawplanet/pets/%d/gallery", request.getOwnerId());

            case POST_MEDIA:
                return String.format("pawplanet/posts/%d", request.getOwnerId());

            case ENCYCLOPEDIA_CLASS:
                return String.format("pawplanet/encyclopedia/classes/%s", request.getSlug());

            case ENCYCLOPEDIA_SPECIES:
                return String.format("pawplanet/encyclopedia/species/%s", request.getSlug());

            case ENCYCLOPEDIA_BREED:
                return String.format("pawplanet/encyclopedia/breeds/%s", request.getSlug());

            default:
                throw new AppException(ErrorCode.INVALID_UPLOAD_CONTEXT);
        }
    }

    /**
     * Optionally determine public_id for specific contexts
     * For avatars, we can use a fixed name to ensure replacement
     * For galleries and posts, we let Cloudinary generate unique IDs
     */
    private String determinePublicId(MediaSignRequest request) {
        UploadContext context = request.getContext();

        switch (context) {
            case USER_AVATAR:
            case PET_AVATAR:
                // For avatars, use fixed name so uploads replace the old avatar
                return "avatar";

            case ENCYCLOPEDIA_CLASS:
            case ENCYCLOPEDIA_SPECIES:
            case ENCYCLOPEDIA_BREED:
                // For encyclopedia, use slug as public_id
                return request.getSlug();

            case PET_GALLERY:
            case POST_MEDIA:
                // For galleries and posts, let Cloudinary generate unique IDs
                return null;

            default:
                return null;
        }
    }

    /**
     * Generate the Cloudinary signature
     */
    private String generateSignature(Map<String, Object> params) {
        try {
            String apiSecret = (String) cloudinary.config.apiSecret;
            return cloudinary.apiSignRequest(params, apiSecret);
        } catch (Exception e) {
            log.error("Failed to generate signature", e);
            throw new AppException(ErrorCode.MEDIA_SIGNATURE_GENERATION_FAILED);
        }
    }

    /**
     * Check if context requires ownerId
     */
    private boolean requiresOwnerId(UploadContext context) {
        return context == UploadContext.USER_AVATAR ||
                context == UploadContext.PET_AVATAR ||
                context == UploadContext.PET_GALLERY ||
                context == UploadContext.POST_MEDIA;
    }

    /**
     * Check if context requires slug
     */
    private boolean requiresSlug(UploadContext context) {
        return context == UploadContext.ENCYCLOPEDIA_CLASS ||
                context == UploadContext.ENCYCLOPEDIA_SPECIES ||
                context == UploadContext.ENCYCLOPEDIA_BREED;
    }

    /**
     * Check if context is PET-related
     */
    private boolean isPetContext(UploadContext context) {
        return context == UploadContext.PET_AVATAR ||
                context == UploadContext.PET_GALLERY;
    }

    /**
     * Verify that the current authenticated user owns the pet
     * For PET contexts, ownerId is actually the petId
     *
     * @param petId The pet ID to verify ownership for
     * @throws AppException if pet not found or user doesn't own the pet
     */
    private void verifyPetOwnership(Long petId) {
        // Get current authenticated user
        String currentUserEmail = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        UserEntity currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // Load the pet
        PetEntity pet = petRepository.findById(petId)
                .orElseThrow(() -> new AppException(ErrorCode.PET_NOT_FOUND));

        // Verify ownership
        if (!pet.getOwnerId().equals(currentUser.getId())) {
            log.warn("User {} attempted to upload media for pet {} owned by user {}",
                    currentUser.getId(), petId, pet.getOwnerId());
            throw new AppException(ErrorCode.UNAUTHORIZED_PET_ACCESS);
        }

        log.debug("Verified user {} owns pet {}", currentUser.getId(), petId);
    }
}