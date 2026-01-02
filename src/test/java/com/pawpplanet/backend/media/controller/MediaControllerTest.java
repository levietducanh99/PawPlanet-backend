package com.pawpplanet.backend.media.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pawpplanet.backend.media.dto.MediaSignRequest;
import com.pawpplanet.backend.media.dto.UploadContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for Media Sign API
 * Tests various upload contexts and validation scenarios
 */
@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "testuser", roles = {"USER"})
class MediaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGenerateSignature_UserAvatar_Success() throws Exception {
        MediaSignRequest request = MediaSignRequest.builder()
                .context(UploadContext.USER_AVATAR)
                .ownerId(123L)
                .build();

        mockMvc.perform(post("/api/v1/media/sign")
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

        mockMvc.perform(post("/api/v1/media/sign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.signature").exists())
                .andExpect(jsonPath("$.asset_folder").value("pawplanet/pets/456/gallery"))
                .andExpect(jsonPath("$.public_id").doesNotExist());
    }

    @Test
    void testGenerateSignature_EncyclopediaBreed_Success() throws Exception {
        MediaSignRequest request = MediaSignRequest.builder()
                .context(UploadContext.ENCYCLOPEDIA_BREED)
                .slug("golden-retriever")
                .build();

        mockMvc.perform(post("/api/v1/media/sign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.signature").exists())
                .andExpect(jsonPath("$.asset_folder").value("pawplanet/encyclopedia/breeds/golden-retriever"))
                .andExpect(jsonPath("$.public_id").value("golden-retriever"));
    }

    @Test
    void testGenerateSignature_PostMedia_WithVideo_Success() throws Exception {
        MediaSignRequest request = MediaSignRequest.builder()
                .context(UploadContext.POST_MEDIA)
                .ownerId(789L)
                .resourceType("video")
                .build();

        mockMvc.perform(post("/api/v1/media/sign")
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

        mockMvc.perform(post("/api/v1/media/sign")
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

        mockMvc.perform(post("/api/v1/media/sign")
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

        mockMvc.perform(post("/api/v1/media/sign")
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

        mockMvc.perform(post("/api/v1/media/sign")
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

        mockMvc.perform(post("/api/v1/media/sign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGenerateSignature_SlugNormalization_Success() throws Exception {
        MediaSignRequest request = MediaSignRequest.builder()
                .context(UploadContext.ENCYCLOPEDIA_SPECIES)
                .slug("Golden-Retriever") // Mixed case - should be normalized
                .build();

        mockMvc.perform(post("/api/v1/media/sign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.asset_folder").value("pawplanet/encyclopedia/species/golden-retriever"))
                .andExpect(jsonPath("$.public_id").value("golden-retriever"));
    }
}

