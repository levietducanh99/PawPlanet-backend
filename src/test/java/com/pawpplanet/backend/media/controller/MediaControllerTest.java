package com.pawpplanet.backend.media.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pawpplanet.backend.common.exception.AppException;
import com.pawpplanet.backend.common.exception.ErrorCode;
import com.pawpplanet.backend.media.dto.MediaSignRequest;
import com.pawpplanet.backend.media.dto.MediaSignResponse;
import com.pawpplanet.backend.media.dto.UploadContext;
import com.pawpplanet.backend.media.service.MediaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for Media Sign API Controller
 * Tests controller layer with mocked service
 */
@WebMvcTest(MediaController.class)
@WithMockUser(username = "testuser", roles = {"USER"})
class MediaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MediaService mediaService;

    @Test
    void testGenerateSignature_UserAvatar_Success() throws Exception {
        MediaSignRequest request = MediaSignRequest.builder()
                .context(UploadContext.USER_AVATAR)
                .ownerId(123L)
                .build();

        MediaSignResponse mockResponse = MediaSignResponse.builder()
                .signature("mock-signature-123")
                .timestamp(1234567890L)
                .apiKey("test-api-key")
                .cloudName("test-cloud")
                .assetFolder("pawplanet/users/123/avatar")
                .publicId("avatar")
                .resourceType("image")
                .build();

        when(mediaService.generateUploadSignature(any(MediaSignRequest.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/api/v1/media/sign")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.signature").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.api_key").exists())
                .andExpect(jsonPath("$.cloud_name").exists())
                .andExpect(jsonPath("$.asset_folder").value("pawplanet/users/123/avatar"))
                .andExpect(jsonPath("$.public_id").value("avatar"))
                .andExpect(jsonPath("$.resource_type").value("image"));
    }

    @Test
    void testGenerateSignature_PetGallery_Success() throws Exception {
        MediaSignRequest request = MediaSignRequest.builder()
                .context(UploadContext.PET_GALLERY)
                .ownerId(456L)
                .build();

        MediaSignResponse mockResponse = MediaSignResponse.builder()
                .signature("mock-signature-456")
                .timestamp(1234567890L)
                .apiKey("test-api-key")
                .cloudName("test-cloud")
                .assetFolder("pawplanet/pets/456/gallery")
                .publicId(null)  // PET_GALLERY doesn't have fixed public_id
                .resourceType("image")
                .build();

        when(mediaService.generateUploadSignature(any(MediaSignRequest.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/api/v1/media/sign")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.signature").exists())
                .andExpect(jsonPath("$.asset_folder").value("pawplanet/pets/456/gallery"))
                .andExpect(jsonPath("$.public_id").doesNotExist());
    }



    @Test
    void testGenerateSignature_PostMedia_WithVideo_Success() throws Exception {
        MediaSignRequest request = MediaSignRequest.builder()
                .context(UploadContext.POST_MEDIA)
                .ownerId(789L)
                .resourceType("video")
                .build();

        MediaSignResponse mockResponse = MediaSignResponse.builder()
                .signature("mock-signature-789")
                .timestamp(1234567890L)
                .apiKey("test-api-key")
                .cloudName("test-cloud")
                .assetFolder("pawplanet/posts/789")
                .publicId(null)
                .resourceType("video")
                .build();

        when(mediaService.generateUploadSignature(any(MediaSignRequest.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/api/v1/media/sign")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.signature").exists())
                .andExpect(jsonPath("$.asset_folder").value("pawplanet/posts/789"))
                .andExpect(jsonPath("$.resource_type").value("video"));
    }

    @Test
    void testGenerateSignature_MissingOwnerId_ShouldFail() throws Exception {
        MediaSignRequest request = MediaSignRequest.builder()
                .context(UploadContext.USER_AVATAR)
                // Missing ownerId
                .build();

        when(mediaService.generateUploadSignature(any(MediaSignRequest.class)))
                .thenThrow(new AppException(ErrorCode.MISSING_OWNER_ID));

        mockMvc.perform(post("/api/v1/media/sign")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGenerateSignature_MissingSlug_ShouldFail() throws Exception {
        MediaSignRequest request = MediaSignRequest.builder()
                .context(UploadContext.ENCYCLOPEDIA_CLASS)
                // Missing slug
                .build();

        when(mediaService.generateUploadSignature(any(MediaSignRequest.class)))
                .thenThrow(new AppException(ErrorCode.MISSING_SLUG));

        mockMvc.perform(post("/api/v1/media/sign")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGenerateSignature_InvalidSlugFormat_ShouldFail() throws Exception {
        MediaSignRequest request = MediaSignRequest.builder()
                .context(UploadContext.ENCYCLOPEDIA_BREED)
                .slug("!!!@@@") // Only special characters - cannot be normalized
                .build();

        when(mediaService.generateUploadSignature(any(MediaSignRequest.class)))
                .thenThrow(new AppException(ErrorCode.INVALID_SLUG_FORMAT));

        mockMvc.perform(post("/api/v1/media/sign")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGenerateSignature_NullContext_ShouldFail() throws Exception {
        MediaSignRequest request = MediaSignRequest.builder()
                .ownerId(123L)
                // Context is null
                .build();

        when(mediaService.generateUploadSignature(any(MediaSignRequest.class)))
                .thenThrow(new AppException(ErrorCode.INVALID_UPLOAD_CONTEXT));

        mockMvc.perform(post("/api/v1/media/sign")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGenerateSignature_NegativeOwnerId_ShouldFail() throws Exception {
        MediaSignRequest request = MediaSignRequest.builder()
                .context(UploadContext.PET_AVATAR)
                .ownerId(-1L)
                .build();

        when(mediaService.generateUploadSignature(any(MediaSignRequest.class)))
                .thenThrow(new AppException(ErrorCode.MISSING_OWNER_ID));

        mockMvc.perform(post("/api/v1/media/sign")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }


}

