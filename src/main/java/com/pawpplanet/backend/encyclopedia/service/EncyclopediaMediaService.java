package com.pawpplanet.backend.encyclopedia.service;

import com.pawpplanet.backend.common.dto.PagedResult;
import com.pawpplanet.backend.common.exception.AppException;
import com.pawpplanet.backend.common.exception.ErrorCode;
import com.pawpplanet.backend.encyclopedia.dto.AddEncyclopediaMediaRequest;
import com.pawpplanet.backend.encyclopedia.dto.AddEncyclopediaMediaResponse;
import com.pawpplanet.backend.encyclopedia.dto.EncyclopediaMediaResponse;
import com.pawpplanet.backend.encyclopedia.entity.EncyclopediaMediaEntity;
import com.pawpplanet.backend.encyclopedia.repository.AnimalClassRepository;
import com.pawpplanet.backend.encyclopedia.repository.BreedRepository;
import com.pawpplanet.backend.encyclopedia.repository.EncyclopediaMediaRepository;
import com.pawpplanet.backend.encyclopedia.repository.SpeciesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EncyclopediaMediaService {

    private final EncyclopediaMediaRepository mediaRepository;
    private final SpeciesRepository speciesRepository;
    private final BreedRepository breedRepository;
    private final AnimalClassRepository animalClassRepository;

    private static final List<String> VALID_MEDIA_TYPES = Arrays.asList("image", "video");
    private static final List<String> VALID_MEDIA_ROLES = Arrays.asList("hero", "gallery", "thumbnail");

    /**
     * Lấy thumbnail (avatar) cho entity
     */
    public String getThumbnailUrl(String entityType, Long entityId) {
        return mediaRepository.findFirstByEntityTypeAndEntityIdAndRole(entityType, entityId, "thumbnail")
                .map(EncyclopediaMediaEntity::getUrl)
                .orElse(null);
    }

    /**
     * Lấy hero banner cho entity
     */
    public String getHeroUrl(String entityType, Long entityId) {
        return mediaRepository.findFirstByEntityTypeAndEntityIdAndRole(entityType, entityId, "hero")
                .map(EncyclopediaMediaEntity::getUrl)
                .orElse(null);
    }

    /**
     * Lấy gallery preview (vài ảnh đầu tiên) cho detail page
     */
    public List<EncyclopediaMediaResponse> getGalleryPreview(String entityType, Long entityId, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return mediaRepository.findTopGalleryByEntityTypeAndEntityId(entityType, entityId, pageable)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Lấy tất cả gallery với pagination
     */
    public PagedResult<EncyclopediaMediaResponse> getGallery(String entityType, Long entityId, int page, int size) {
        // Convert 1-based page to 0-based
        int zeroBasedPage = Math.max(0, page - 1);
        Pageable pageable = PageRequest.of(zeroBasedPage, Math.max(1, size));

        Page<EncyclopediaMediaEntity> p = mediaRepository.findGalleryByEntityTypeAndEntityId(
                entityType, entityId, pageable);

        PagedResult<EncyclopediaMediaResponse> result = new PagedResult<>();
        result.setItems(p.getContent().stream().map(this::toDto).collect(Collectors.toList()));
        result.setTotalElements(p.getTotalElements());
        result.setPage(p.getNumber() + 1);  // Convert back to 1-based
        result.setSize(p.getSize());
        return result;
    }

    private EncyclopediaMediaResponse toDto(EncyclopediaMediaEntity e) {
        EncyclopediaMediaResponse r = new EncyclopediaMediaResponse();
        r.setId(e.getId());
        r.setEntityType(e.getEntityType());
        r.setEntityId(e.getEntityId());
        r.setType(e.getType());
        r.setRole(e.getRole());
        r.setUrl(e.getUrl());
        r.setDisplayOrder(e.getDisplayOrder());
        return r;
    }

    /**
     * Admin only: Add media to a species
     */
    @Transactional
    public AddEncyclopediaMediaResponse addMediaToSpecies(Long speciesId, AddEncyclopediaMediaRequest request) {
        // Validate species exists
        if (!speciesRepository.existsById(speciesId)) {
            throw new AppException(ErrorCode.SPECIES_NOT_FOUND);
        }

        return addMediaToEntity("species", speciesId, request);
    }

    /**
     * Admin only: Add media to a breed
     */
    @Transactional
    public AddEncyclopediaMediaResponse addMediaToBreed(Long breedId, AddEncyclopediaMediaRequest request) {
        // Validate breed exists
        if (!breedRepository.existsById(breedId)) {
            throw new AppException(ErrorCode.BREED_NOT_FOUND);
        }

        return addMediaToEntity("breed", breedId, request);
    }

    /**
     * Admin only: Add media to an animal class
     */
    @Transactional
    public AddEncyclopediaMediaResponse addMediaToClass(Long classId, AddEncyclopediaMediaRequest request) {
        // Validate class exists
        if (!animalClassRepository.existsById(classId)) {
            throw new AppException(ErrorCode.ANIMAL_CLASS_NOT_FOUND);
        }

        return addMediaToEntity("class", classId, request);
    }

    /**
     * Common method to add media to any encyclopedia entity
     */
    private AddEncyclopediaMediaResponse addMediaToEntity(String entityType, Long entityId, AddEncyclopediaMediaRequest request) {
        // Validate request
        validateMediaRequest(request);

        // Get current max display order for gallery items
        int currentMaxOrder = getCurrentMaxDisplayOrder(entityType, entityId);

        List<EncyclopediaMediaEntity> savedEntities = new ArrayList<>();
        int displayOrder = currentMaxOrder;

        for (AddEncyclopediaMediaRequest.MediaItem item : request.getMediaItems()) {
            EncyclopediaMediaEntity entity = new EncyclopediaMediaEntity();
            entity.setEntityType(entityType);
            entity.setEntityId(entityId);
            entity.setType(item.getType().toLowerCase());
            entity.setRole(item.getRole().toLowerCase());
            entity.setUrl(item.getUrl());

            // Only increment display order for gallery items
            if ("gallery".equals(item.getRole().toLowerCase())) {
                displayOrder++;
                entity.setDisplayOrder(displayOrder);
            } else {
                // For hero and thumbnail, use 0 or don't set display order
                entity.setDisplayOrder(0);
            }

            savedEntities.add(mediaRepository.save(entity));
        }

        // Build response
        AddEncyclopediaMediaResponse response = new AddEncyclopediaMediaResponse();
        response.setAddedMedia(savedEntities.stream()
                .map(this::toDto)
                .collect(Collectors.toList()));
        response.setTotalCount(savedEntities.size());
        response.setMessage("Successfully added " + savedEntities.size() + " media item(s) to " + entityType);

        return response;
    }

    /**
     * Validate media request
     */
    private void validateMediaRequest(AddEncyclopediaMediaRequest request) {
        for (AddEncyclopediaMediaRequest.MediaItem item : request.getMediaItems()) {
            // Validate type
            if (!VALID_MEDIA_TYPES.contains(item.getType().toLowerCase())) {
                throw new AppException(ErrorCode.INVALID_MEDIA_TYPE);
            }

            // Validate role
            if (!VALID_MEDIA_ROLES.contains(item.getRole().toLowerCase())) {
                throw new AppException(ErrorCode.INVALID_MEDIA_ROLE);
            }

            // Validate URL
            if (item.getUrl() == null || item.getUrl().trim().isEmpty()) {
                throw new AppException(ErrorCode.INVALID_CREDENTIALS); // Reuse existing error or add new one
            }
        }
    }

    /**
     * Get current max display order for gallery items
     */
    private int getCurrentMaxDisplayOrder(String entityType, Long entityId) {
        return mediaRepository.findMaxDisplayOrderByEntityTypeAndEntityIdAndRole(entityType, entityId, "gallery")
                .orElse(0);
    }
}


