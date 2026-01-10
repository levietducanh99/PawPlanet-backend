package com.pawpplanet.backend.search.service.impl;

import com.pawpplanet.backend.common.exception.AppException;
import com.pawpplanet.backend.common.exception.ErrorCode;
import com.pawpplanet.backend.encyclopedia.entity.BreedEntity;
import com.pawpplanet.backend.encyclopedia.entity.SpeciesEntity;
import com.pawpplanet.backend.encyclopedia.repository.BreedRepository;
import com.pawpplanet.backend.encyclopedia.repository.SpeciesRepository;
import com.pawpplanet.backend.pet.entity.PetEntity;
import com.pawpplanet.backend.pet.entity.PetMediaEntity;
import com.pawpplanet.backend.pet.repository.PetMediaRepository;
import com.pawpplanet.backend.pet.repository.PetRepository;
import com.pawpplanet.backend.search.dto.GlobalSearchResponse;
import com.pawpplanet.backend.search.dto.SearchPetDTO;
import com.pawpplanet.backend.search.dto.SearchUserDTO;
import com.pawpplanet.backend.search.service.GlobalSearchService;
import com.pawpplanet.backend.user.entity.UserEntity;
import com.pawpplanet.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GlobalSearchServiceImpl implements GlobalSearchService {

    private final UserRepository userRepository;
    private final PetRepository petRepository;
    private final BreedRepository breedRepository;
    private final SpeciesRepository speciesRepository;
    private final PetMediaRepository petMediaRepository;

    private static final int DEFAULT_LIMIT = 10;

    @Override
    public GlobalSearchResponse search(String keyword, String types, Integer limit) {
        // Validate keyword
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new AppException(ErrorCode.INVALID_REQUEST);
        }
        
        String trimmedKeyword = keyword.trim();
        if (trimmedKeyword.length() < 2) {
            throw new AppException(ErrorCode.INVALID_REQUEST);
        }

        // Set default limit
        int resultLimit = (limit != null && limit > 0) ? limit : DEFAULT_LIMIT;
        Pageable pageable = PageRequest.of(0, resultLimit);

        // Determine which types to search
        boolean searchUsers = shouldSearchType(types, "user");
        boolean searchPets = shouldSearchType(types, "pet");

        GlobalSearchResponse response = new GlobalSearchResponse();

        // Search users
        if (searchUsers) {
            List<UserEntity> users = userRepository.searchByUsername(trimmedKeyword, pageable);
            response.setUsers(users.stream()
                    .map(this::mapToSearchUserDTO)
                    .collect(Collectors.toList()));
        }

        // Search pets
        if (searchPets) {
            List<PetEntity> pets = petRepository.searchByNameOrBreed(trimmedKeyword, pageable);
            response.setPets(pets.stream()
                    .map(this::mapToSearchPetDTO)
                    .collect(Collectors.toList()));
        }

        return response;
    }

    private boolean shouldSearchType(String types, String type) {
        // If types is null or empty, search all types
        if (types == null || types.trim().isEmpty()) {
            return true;
        }
        
        // Check if the specific type is included in the comma-separated list
        return Arrays.stream(types.split(","))
                .map(String::trim)
                .anyMatch(t -> t.equalsIgnoreCase(type));
    }

    private SearchUserDTO mapToSearchUserDTO(UserEntity user) {
        return SearchUserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .avatarUrl(user.getAvatarUrl())
                .build();
    }

    private SearchPetDTO mapToSearchPetDTO(PetEntity pet) {
        // Get species name
        String speciesName = null;
        if (pet.getSpeciesId() != null) {
            speciesName = speciesRepository.findById(pet.getSpeciesId())
                    .map(SpeciesEntity::getName)
                    .orElse(null);
        }

        // Get breed name
        String breedName = null;
        if (pet.getBreedId() != null) {
            breedName = breedRepository.findById(pet.getBreedId())
                    .map(BreedEntity::getName)
                    .orElse(null);
        }

        // Get avatar URL (first media with role=avatar and type=image)
        String avatarUrl = null;
        List<PetMediaEntity> mediaList = petMediaRepository.findByPetId(pet.getId());
        if (!mediaList.isEmpty()) {
            avatarUrl = mediaList.stream()
                    .filter(m -> "avatar".equals(m.getRole()) && "image".equals(m.getType()))
                    .min(Comparator.comparing(PetMediaEntity::getDisplayOrder, 
                            Comparator.nullsLast(Comparator.naturalOrder())))
                    .map(PetMediaEntity::getUrl)
                    .orElse(null);
        }

        return SearchPetDTO.builder()
                .id(pet.getId())
                .name(pet.getName())
                .species(speciesName)
                .breed(breedName)
                .avatarUrl(avatarUrl)
                .build();
    }
}
