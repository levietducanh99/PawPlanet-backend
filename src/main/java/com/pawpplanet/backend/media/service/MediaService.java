package com.pawpplanet.backend.media.service;

import com.pawpplanet.backend.media.dto.MediaSignRequest;
import com.pawpplanet.backend.media.dto.MediaSignResponse;

import java.util.Map;

public interface MediaService {
    /**
     * Legacy method - deprecated
     * Use generateUploadSignature instead
     */
    @Deprecated
    Map<String, Object> getUploadSignature();

    /**
     * Generate a signed upload signature for Cloudinary uploads.
     * This method determines the correct folder structure based on context
     * and returns all required parameters for frontend to upload directly to Cloudinary.
     *
     * @param request MediaSignRequest containing context and identifier information
     * @return MediaSignResponse with signature, timestamp, and upload parameters
     * @throws com.pawpplanet.backend.common.exception.AppException if validation fails
     */
    MediaSignResponse generateUploadSignature(MediaSignRequest request);
}


