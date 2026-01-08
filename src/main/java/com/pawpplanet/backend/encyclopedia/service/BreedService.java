package com.pawpplanet.backend.encyclopedia.service;

import com.pawpplanet.backend.common.dto.PagedResult;
import com.pawpplanet.backend.encyclopedia.dto.BreedResponse;
import com.pawpplanet.backend.encyclopedia.dto.BreedDetailResponse;
import com.pawpplanet.backend.encyclopedia.dto.BreedAttributeResponse;
import com.pawpplanet.backend.encyclopedia.dto.BreedSectionContentResponse;
import com.pawpplanet.backend.encyclopedia.entity.BreedEntity;
import com.pawpplanet.backend.encyclopedia.entity.BreedAttributeEntity;
import com.pawpplanet.backend.encyclopedia.entity.BreedSectionContentEntity;
import com.pawpplanet.backend.encyclopedia.repository.BreedRepository;
import com.pawpplanet.backend.encyclopedia.repository.BreedAttributeRepository;
import com.pawpplanet.backend.encyclopedia.repository.BreedSectionContentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BreedService {

    private final BreedRepository breedRepository;
    private final BreedAttributeRepository breedAttributeRepository;
    private final BreedSectionContentRepository breedSectionContentRepository;
    private final EncyclopediaMediaService mediaService;

    public PagedResult<BreedResponse> getBreeds(int page, int size, Long speciesId, String taxonomyType) {
        // Convert 1-based page (from API) to 0-based page (for Spring Data)
        int zeroBasedPage = Math.max(0, page - 1);
        Pageable pageable = PageRequest.of(zeroBasedPage, Math.max(1, size));
        Page<BreedEntity> p;
        if (speciesId != null && taxonomyType != null) {
            p = breedRepository.findBySpeciesIdAndTaxonomyType(speciesId, taxonomyType, pageable);
        } else if (speciesId != null) {
            p = breedRepository.findBySpeciesId(speciesId, pageable);
        } else if (taxonomyType != null) {
            p = breedRepository.findByTaxonomyType(taxonomyType, pageable);
        } else {
            p = breedRepository.findAll(pageable);
        }

        PagedResult<BreedResponse> result = new PagedResult<>();
        result.setItems(p.getContent().stream().map(this::toDto).collect(Collectors.toList()));
        result.setTotalElements(p.getTotalElements());
        result.setPage(p.getNumber() + 1);  // Convert back to 1-based for response
        result.setSize(p.getSize());
        return result;
    }

    public List<BreedResponse> getBySpeciesId(Long speciesId) {
        return breedRepository.findBySpeciesId(speciesId).stream().map(this::toDto).collect(Collectors.toList());
    }

    public Optional<BreedResponse> getById(Long id) {
        return breedRepository.findById(id).map(this::toDto);
    }

    public Optional<BreedDetailResponse> getDetailById(Long id) {
        return breedRepository.findById(id).map(this::toDetailDto);
    }

    private BreedResponse toDto(BreedEntity e) {
        BreedResponse r = new BreedResponse();
        r.setId(e.getId());
        r.setSpeciesId(e.getSpeciesId());
        r.setName(e.getName());
        r.setSlug(e.getSlug());
        r.setOrigin(e.getOrigin());
        r.setShortDescription(e.getShortDescription());
        r.setTaxonomyType(e.getTaxonomyType());
        r.setAvatarUrl(mediaService.getThumbnailUrl("breed", e.getId()));
        return r;
    }

    private BreedDetailResponse toDetailDto(BreedEntity e) {
        BreedDetailResponse r = new BreedDetailResponse();
        r.setId(e.getId());
        r.setSpeciesId(e.getSpeciesId());
        r.setName(e.getName());
        r.setSlug(e.getSlug());
        r.setOrigin(e.getOrigin());
        r.setShortDescription(e.getShortDescription());
        r.setTaxonomyType(e.getTaxonomyType());

        // Load media
        r.setHeroUrl(mediaService.getHeroUrl("breed", e.getId()));
        r.setThumbnailUrl(mediaService.getThumbnailUrl("breed", e.getId()));
        r.setGalleryPreview(mediaService.getGalleryPreview("breed", e.getId(), 5));  // First 5 images

        // Load attributes
        r.setAttributes(breedAttributeRepository.findByBreedIdOrderByDisplayOrderAsc(e.getId())
                .stream()
                .map(this::toAttributeDto)
                .collect(Collectors.toList()));

        // Load sections
        r.setSections(breedSectionContentRepository.findByBreedIdOrderByDisplayOrderAsc(e.getId())
                .stream()
                .map(this::toSectionContentDto)
                .collect(Collectors.toList()));

        return r;
    }

    private BreedAttributeResponse toAttributeDto(BreedAttributeEntity e) {
        BreedAttributeResponse r = new BreedAttributeResponse();
        r.setId(e.getId());
        r.setBreedId(e.getBreedId());
        r.setKey(e.getKey());
        r.setValue(e.getValue());
        r.setDisplayOrder(e.getDisplayOrder());
        return r;
    }

    private BreedSectionContentResponse toSectionContentDto(BreedSectionContentEntity e) {
        BreedSectionContentResponse r = new BreedSectionContentResponse();
        r.setId(e.getId());
        r.setBreedId(e.getBreed() != null ? e.getBreed().getId() : null);
        r.setSectionId(e.getSection() != null ? e.getSection().getId() : null);
        r.setSectionCode(e.getSection() != null ? e.getSection().getCode() : null);
        r.setSectionName(e.getSection() != null ? e.getSection().getDisplayName() : null);
        r.setLanguage(e.getLanguage());
        r.setContent(e.getContent());
        r.setDisplayOrder(e.getDisplayOrder());
        return r;
    }
}

