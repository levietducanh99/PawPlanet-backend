package com.pawpplanet.backend.media.dto;

/**
 * Enum representing different upload contexts for Cloudinary uploads.
 * Each context maps to a specific folder structure in Cloudinary.
 */
public enum UploadContext {
    /**
     * User avatar image
     * Folder: pawplanet/users/{userId}/avatar
     * Requires: ownerId (userId)
     */
    USER_AVATAR,

    /**
     * Pet avatar image
     * Folder: pawplanet/pets/{petId}/avatar
     * Requires: ownerId (interpreted as petId)
     * Authorization: User must own the pet
     */
    PET_AVATAR,

    /**
     * Pet gallery images
     * Folder: pawplanet/pets/{petId}/gallery
     * Requires: ownerId (interpreted as petId)
     * Authorization: User must own the pet
     */
    PET_GALLERY,

    /**
     * Post media (images/videos)
     * Folder: pawplanet/posts/{postId}
     * Requires: ownerId (postId)
     */
    POST_MEDIA,

    /**
     * Encyclopedia class images
     * Folder: pawplanet/encyclopedia/classes/{slug}
     * Requires: slug
     */
    ENCYCLOPEDIA_CLASS,

    /**
     * Encyclopedia species images
     * Folder: pawplanet/encyclopedia/species/{slug}
     * Requires: slug
     */
    ENCYCLOPEDIA_SPECIES,

    /**
     * Encyclopedia breed images
     * Folder: pawplanet/encyclopedia/breeds/{slug}
     * Requires: slug
     */
    ENCYCLOPEDIA_BREED
}

