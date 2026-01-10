package com.pawpplanet.backend.pet.controller;

import com.pawpplanet.backend.pet.service.PetService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for Pet Media Delete API
 * Tests soft delete functionality for pet gallery images
 */
@WebMvcTest(PetController.class)
@WithMockUser(username = "testuser", roles = {"USER"})
class PetControllerDeleteTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PetService petService;

    @Test
    void testDeletePetMedia_Success() throws Exception {
        Long petId = 1L;
        Long mediaId = 100L;

        doNothing().when(petService).deletePetMedia(eq(petId), eq(mediaId));

        mockMvc.perform(delete("/api/v1/pets/{petId}/gallery/{mediaId}", petId, mediaId)
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeletePetMedia_PetNotFound() throws Exception {
        Long petId = 999L;
        Long mediaId = 100L;

        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Pet not found"))
                .when(petService).deletePetMedia(eq(petId), eq(mediaId));

        mockMvc.perform(delete("/api/v1/pets/{petId}/gallery/{mediaId}", petId, mediaId)
                        .with(csrf()))
                .andExpect(status().isBadRequest()); // GlobalExceptionHandler returns 400 for RuntimeException
    }

    @Test
    void testDeletePetMedia_MediaNotFound() throws Exception {
        Long petId = 1L;
        Long mediaId = 999L;

        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Media not found"))
                .when(petService).deletePetMedia(eq(petId), eq(mediaId));

        mockMvc.perform(delete("/api/v1/pets/{petId}/gallery/{mediaId}", petId, mediaId)
                        .with(csrf()))
                .andExpect(status().isBadRequest()); // GlobalExceptionHandler returns 400 for RuntimeException
    }

    @Test
    void testDeletePetMedia_Forbidden_NotOwner() throws Exception {
        Long petId = 1L;
        Long mediaId = 100L;

        doThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to delete media from this pet"))
                .when(petService).deletePetMedia(eq(petId), eq(mediaId));

        mockMvc.perform(delete("/api/v1/pets/{petId}/gallery/{mediaId}", petId, mediaId)
                        .with(csrf()))
                .andExpect(status().isBadRequest()); // GlobalExceptionHandler returns 400 for RuntimeException
    }

    @Test
    void testDeletePetMedia_MediaNotBelongToPet() throws Exception {
        Long petId = 1L;
        Long mediaId = 100L;

        doThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "This media does not belong to the specified pet"))
                .when(petService).deletePetMedia(eq(petId), eq(mediaId));

        mockMvc.perform(delete("/api/v1/pets/{petId}/gallery/{mediaId}", petId, mediaId)
                        .with(csrf()))
                .andExpect(status().isBadRequest()); // GlobalExceptionHandler returns 400 for RuntimeException
    }
}
