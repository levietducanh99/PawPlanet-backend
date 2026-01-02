package com.pawpplanet.backend.media.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO containing all required parameters for Cloudinary upload.
 * Frontend uses these values to upload directly to Cloudinary.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaSignResponse {

    /**
     * Generated signature for secure upload
     */
    private String signature;

    /**
     * Unix timestamp (seconds) used in signature generation
     */
    private Long timestamp;

    /**
     * Cloudinary API key (safe to expose)
     */
    @JsonProperty("api_key")
    private String apiKey;

    /**
     * Cloudinary cloud name (safe to expose)
     */
    @JsonProperty("cloud_name")
    private String cloudName;

    /**
     * Determined asset folder path
     * Frontend MUST use this exact value
     */
    @JsonProperty("asset_folder")
    private String assetFolder;

    /**
     * Optional pre-determined public ID
     * If present, frontend MUST use this exact value
     */
    @JsonProperty("public_id")
    private String publicId;

    /**
     * Resource type for upload
     * Default: image
     */
    @JsonProperty("resource_type")
    private String resourceType;
}

