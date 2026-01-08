package com.pawpplanet.backend.encyclopedia.service;

import com.pawpplanet.backend.common.dto.PagedResult;
import com.pawpplanet.backend.encyclopedia.dto.*;
import com.pawpplanet.backend.encyclopedia.entity.SpeciesEntity;
import com.pawpplanet.backend.encyclopedia.entity.SpeciesAttributeEntity;
import com.pawpplanet.backend.encyclopedia.entity.SpeciesSectionContentEntity;
import com.pawpplanet.backend.encyclopedia.repository.SpeciesRepository;
import com.pawpplanet.backend.encyclopedia.repository.SpeciesAttributeRepository;
import com.pawpplanet.backend.encyclopedia.repository.SpeciesSectionContentRepository;
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
public class SpeciesService {

    private final SpeciesRepository speciesRepository;
    private final SpeciesAttributeRepository speciesAttributeRepository;
    private final SpeciesSectionContentRepository speciesSectionContentRepository;
    private final EncyclopediaMediaService mediaService;

    public PagedResult<SpeciesResponse> getSpecies(int page, int size, Long classId) {
        // Convert 1-based page (from API) to 0-based page (for Spring Data)
        int zeroBasedPage = Math.max(0, page - 1);
        Pageable pageable = PageRequest.of(zeroBasedPage, Math.max(1, size));
        Page<SpeciesEntity> p;
        if (classId != null) {
            p = speciesRepository.findByClassId(classId, pageable);
        } else {
            p = speciesRepository.findAll(pageable);
        }

        PagedResult<SpeciesResponse> result = new PagedResult<>();
        result.setItems(p.getContent().stream().map(this::toDto).collect(Collectors.toList()));
        result.setTotalElements(p.getTotalElements());
        result.setPage(p.getNumber() + 1);  // Convert back to 1-based for response
        result.setSize(p.getSize());
        return result;
    }

    // New: search species by keyword (name or scientificName), paged; page param is 1-based
    public PagedResult<SpeciesResponse> searchSpecies(String q, int page, int size) {
        String keyword = q == null ? "" : q.trim();
        int zeroBasedPage = Math.max(0, page - 1);
        Pageable pageable = PageRequest.of(zeroBasedPage, Math.max(1, size));
        Page<SpeciesEntity> p = speciesRepository.findByNameContainingIgnoreCaseOrScientificNameContainingIgnoreCase(keyword, keyword, pageable);

        PagedResult<SpeciesResponse> result = new PagedResult<>();
        result.setItems(p.getContent().stream().map(this::toDto).collect(Collectors.toList()));
        result.setTotalElements(p.getTotalElements());
        result.setPage(p.getNumber() + 1);
        result.setSize(p.getSize());
        return result;
    }

    public Optional<SpeciesResponse> getById(Long id) {
        return speciesRepository.findById(id).map(this::toDto);
    }

    public Optional<SpeciesDetailResponse> getDetailById(Long id) {
        return speciesRepository.findById(id).map(this::toDetailDto);
    }

    // New: list species by classId (non-paged)
    public List<SpeciesResponse> getByClassId(Long classId) {
        List<SpeciesEntity> list = speciesRepository.findByClassId(classId);
        return list.stream().map(this::toDto).collect(Collectors.toList());
    }

    private SpeciesResponse toDto(SpeciesEntity e) {
        SpeciesResponse r = new SpeciesResponse();
        r.setId(e.getId());
        r.setClassId(e.getClassId());
        r.setName(e.getName());
        r.setSlug(e.getSlug());
        r.setScientificName(e.getScientificName());
        r.setDescription(e.getDescription());
        r.setAvatarUrl(mediaService.getThumbnailUrl("species", e.getId()));
        return r;
    }

    private SpeciesDetailResponse toDetailDto(SpeciesEntity e) {
        SpeciesDetailResponse r = new SpeciesDetailResponse();
        r.setId(e.getId());
        r.setClassId(e.getClassId());
        r.setName(e.getName());
        r.setSlug(e.getSlug());
        r.setScientificName(e.getScientificName());
        r.setDescription(e.getDescription());

        // Load media
        r.setHeroUrl(mediaService.getHeroUrl("species", e.getId()));
        r.setThumbnailUrl(mediaService.getThumbnailUrl("species", e.getId()));
        r.setGalleryPreview(mediaService.getGalleryPreview("species", e.getId(), 5));  // First 5 images

        // Load attributes
        r.setAttributes(speciesAttributeRepository.findBySpeciesIdOrderByDisplayOrderAsc(e.getId())
                .stream()
                .map(this::toAttributeDto)
                .collect(Collectors.toList()));

        // Load sections
        r.setSections(speciesSectionContentRepository.findBySpeciesIdOrderByDisplayOrderAsc(e.getId())
                .stream()
                .map(this::toSectionContentDto)
                .collect(Collectors.toList()));

        return r;
    }

    private SpeciesAttributeResponse toAttributeDto(SpeciesAttributeEntity e) {
        SpeciesAttributeResponse r = new SpeciesAttributeResponse();
        r.setId(e.getId());
        r.setSpeciesId(e.getSpeciesId());
        r.setKey(e.getKey());
        r.setValueMin(e.getValueMin());
        r.setValueMax(e.getValueMax());
        r.setUnit(e.getUnit());
        r.setDisplayOrder(e.getDisplayOrder());
        return r;
    }

    private SpeciesSectionContentResponse toSectionContentDto(SpeciesSectionContentEntity e) {
        SpeciesSectionContentResponse r = new SpeciesSectionContentResponse();
        r.setId(e.getId());
        r.setSpeciesId(e.getSpeciesId());
        r.setSectionId(e.getSectionId());
        r.setSectionCode(e.getSection() != null ? e.getSection().getCode() : null);
        r.setSectionName(e.getSection() != null ? e.getSection().getDisplayName() : null);
        r.setLanguage(e.getLanguage());
        r.setContent(e.getContent());
        r.setDisplayOrder(e.getDisplayOrder());
        return r;
    }
}
