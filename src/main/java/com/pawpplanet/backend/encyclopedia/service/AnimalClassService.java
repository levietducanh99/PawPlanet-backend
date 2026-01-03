package com.pawpplanet.backend.encyclopedia.service;

import com.pawpplanet.backend.encyclopedia.dto.AnimalClassResponse;
import com.pawpplanet.backend.encyclopedia.entity.AnimalClassEntity;
import com.pawpplanet.backend.encyclopedia.repository.AnimalClassRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnimalClassService {

    private final AnimalClassRepository repository;
    private final EncyclopediaMediaService mediaService;

    public List<AnimalClassResponse> getAllClasses() {
        return repository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public Optional<AnimalClassResponse> getById(Long id) {
        return repository.findById(id).map(this::toDto);
    }

    public Optional<AnimalClassResponse> getByCode(String code) {
        return repository.findByCode(code).map(this::toDto);
    }

    private AnimalClassResponse toDto(AnimalClassEntity e) {
        AnimalClassResponse r = new AnimalClassResponse();
        r.setId(e.getId());
        r.setName(e.getName());
        r.setCode(e.getCode());
        r.setSlug(e.getSlug());
        r.setDescription(e.getDescription());
        r.setAvatarUrl(mediaService.getThumbnailUrl("class", e.getId()));
        return r;
    }
}

