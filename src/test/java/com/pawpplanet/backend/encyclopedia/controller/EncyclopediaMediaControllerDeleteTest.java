package com.pawpplanet.backend.encyclopedia.controller;

import com.pawpplanet.backend.common.exception.AppException;
import com.pawpplanet.backend.common.exception.ErrorCode;
import com.pawpplanet.backend.encyclopedia.service.EncyclopediaMediaService;
import com.pawpplanet.backend.user.entity.UserEntity;
import com.pawpplanet.backend.user.repository.UserRepository;
import com.pawpplanet.backend.utils.SecurityHelper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for Encyclopedia Media Delete API
 * Tests soft delete functionality for encyclopedia media (admin only)
 */
@WebMvcTest(EncyclopediaMediaController.class)
class EncyclopediaMediaControllerDeleteTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EncyclopediaMediaService mediaService;

    @MockBean
    private SecurityHelper securityHelper;

    @MockBean
    private UserRepository userRepository;

    @Test
    @WithMockUser(username = "admin@test.com", roles = {"ADMIN"})
    void testDeleteEncyclopediaMedia_Success_AsAdmin() throws Exception {
        Long mediaId = 100L;
        Long adminUserId = 1L;

        // Mock admin check
        UserEntity admin = new UserEntity();
        admin.setId(adminUserId);
        admin.setEmail("admin@test.com");
        admin.setRole("ADMIN");
        
        when(userRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(admin));
        when(securityHelper.getCurrentUser()).thenReturn(admin);
        when(securityHelper.isAdmin()).thenReturn(true);
        doNothing().when(securityHelper).requireAdmin();
        when(securityHelper.getCurrentUserId()).thenReturn(adminUserId);

        doNothing().when(mediaService).deleteEncyclopediaMedia(eq(mediaId), eq(adminUserId));

        mockMvc.perform(delete("/api/v1/encyclopedia/media/{mediaId}", mediaId)
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(securityHelper).requireAdmin();
        verify(mediaService).deleteEncyclopediaMedia(eq(mediaId), eq(adminUserId));
    }

    @Test
    @WithMockUser(username = "user@test.com", roles = {"USER"})
    void testDeleteEncyclopediaMedia_Forbidden_NotAdmin() throws Exception {
        Long mediaId = 100L;

        // Mock non-admin user
        UserEntity user = new UserEntity();
        user.setId(2L);
        user.setEmail("user@test.com");
        user.setRole("USER");
        
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
        when(securityHelper.getCurrentUser()).thenReturn(user);
        when(securityHelper.isAdmin()).thenReturn(false);
        doThrow(new AppException(ErrorCode.ADMIN_ACCESS_REQUIRED))
                .when(securityHelper).requireAdmin();

        mockMvc.perform(delete("/api/v1/encyclopedia/media/{mediaId}", mediaId)
                        .with(csrf()))
                .andExpect(status().isBadRequest()); // GlobalExceptionHandler returns 400 for AppException

        verify(securityHelper).requireAdmin();
        verify(mediaService, never()).deleteEncyclopediaMedia(any(), any());
    }

    @Test
    @WithMockUser(username = "admin@test.com", roles = {"ADMIN"})
    void testDeleteEncyclopediaMedia_MediaNotFound() throws Exception {
        Long mediaId = 999L;
        Long adminUserId = 1L;

        // Mock admin check
        UserEntity admin = new UserEntity();
        admin.setId(adminUserId);
        admin.setEmail("admin@test.com");
        admin.setRole("ADMIN");
        
        when(userRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(admin));
        when(securityHelper.getCurrentUser()).thenReturn(admin);
        when(securityHelper.isAdmin()).thenReturn(true);
        doNothing().when(securityHelper).requireAdmin();
        when(securityHelper.getCurrentUserId()).thenReturn(adminUserId);

        doThrow(new AppException(ErrorCode.ENCYCLOPEDIA_MEDIA_NOT_FOUND))
                .when(mediaService).deleteEncyclopediaMedia(eq(mediaId), eq(adminUserId));

        mockMvc.perform(delete("/api/v1/encyclopedia/media/{mediaId}", mediaId)
                        .with(csrf()))
                .andExpect(status().isBadRequest()); // GlobalExceptionHandler returns 400 for AppException

        verify(securityHelper).requireAdmin();
        verify(mediaService).deleteEncyclopediaMedia(eq(mediaId), eq(adminUserId));
    }
}
