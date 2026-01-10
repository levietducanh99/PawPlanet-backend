package com.pawpplanet.backend.search.controller;

import com.pawpplanet.backend.search.dto.GlobalSearchResponse;
import com.pawpplanet.backend.search.dto.SearchPetDTO;
import com.pawpplanet.backend.search.dto.SearchUserDTO;
import com.pawpplanet.backend.search.service.GlobalSearchService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
    controllers = GlobalSearchController.class,
    excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
        org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration.class
    }
)
class GlobalSearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GlobalSearchService globalSearchService;

    @Test
    void searchShouldReturnBothUsersAndPets() throws Exception {
        // Arrange
        SearchUserDTO user = SearchUserDTO.builder()
                .id(1L)
                .username("testuser")
                .fullName("Test User")
                .avatarUrl("http://example.com/avatar.jpg")
                .build();

        SearchPetDTO pet = SearchPetDTO.builder()
                .id(1L)
                .name("Milo")
                .species("Dog")
                .breed("Corgi")
                .avatarUrl("http://example.com/pet.jpg")
                .build();

        GlobalSearchResponse response = GlobalSearchResponse.builder()
                .users(Collections.singletonList(user))
                .pets(Collections.singletonList(pet))
                .build();

        when(globalSearchService.search(eq("test"), isNull(), isNull()))
                .thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/api/v1/search")
                        .param("q", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.users").isArray())
                .andExpect(jsonPath("$.users[0].id").value(1))
                .andExpect(jsonPath("$.users[0].username").value("testuser"))
                .andExpect(jsonPath("$.pets").isArray())
                .andExpect(jsonPath("$.pets[0].id").value(1))
                .andExpect(jsonPath("$.pets[0].name").value("Milo"));
    }

    @Test
    void searchShouldReturnOnlyUsersWhenTypesIsUser() throws Exception {
        // Arrange
        SearchUserDTO user = SearchUserDTO.builder()
                .id(1L)
                .username("ducanh")
                .fullName("Duc Anh")
                .build();

        GlobalSearchResponse response = GlobalSearchResponse.builder()
                .users(Collections.singletonList(user))
                .build();

        when(globalSearchService.search(eq("duc"), eq("user"), isNull()))
                .thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/api/v1/search")
                        .param("q", "duc")
                        .param("types", "user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.users").isArray())
                .andExpect(jsonPath("$.users[0].username").value("ducanh"))
                .andExpect(jsonPath("$.pets").doesNotExist());
    }

    @Test
    void searchShouldReturnOnlyPetsWhenTypesIsPet() throws Exception {
        // Arrange
        SearchPetDTO pet = SearchPetDTO.builder()
                .id(1L)
                .name("Corgi")
                .species("Dog")
                .breed("Corgi")
                .build();

        GlobalSearchResponse response = GlobalSearchResponse.builder()
                .pets(Collections.singletonList(pet))
                .build();

        when(globalSearchService.search(eq("corgi"), eq("pet"), isNull()))
                .thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/api/v1/search")
                        .param("q", "corgi")
                        .param("types", "pet"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pets").isArray())
                .andExpect(jsonPath("$.pets[0].name").value("Corgi"))
                .andExpect(jsonPath("$.users").doesNotExist());
    }

    @Test
    void searchShouldAcceptLimitParameter() throws Exception {
        // Arrange
        GlobalSearchResponse response = GlobalSearchResponse.builder()
                .users(Collections.emptyList())
                .pets(Collections.emptyList())
                .build();

        when(globalSearchService.search(eq("test"), isNull(), eq(5)))
                .thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/api/v1/search")
                        .param("q", "test")
                        .param("limit", "5"))
                .andExpect(status().isOk());
    }
}
