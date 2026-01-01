package com.pawpplanet.backend.encyclopedia.service;

import com.pawpplanet.backend.common.dto.PagedResult;
import com.pawpplanet.backend.encyclopedia.dto.SpeciesResponse;
import com.pawpplanet.backend.encyclopedia.entity.SpeciesEntity;
import com.pawpplanet.backend.encyclopedia.repository.SpeciesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SpeciesService {

    private final SpeciesRepository speciesRepository;

    public PagedResult<SpeciesResponse> getSpecies(int page, int size, Long classId) {
        Pageable pageable = PageRequest.of(Math.max(0, page), Math.max(1, size));
        Page<SpeciesEntity> p;
        if (classId != null) {
            p = speciesRepository.findByClassId(classId, pageable);
        } else {
            p = speciesRepository.findAll(pageable);
        }

        PagedResult<SpeciesResponse> result = new PagedResult<>();
        result.setItems(p.getContent().stream().map(this::toDto).collect(Collectors.toList()));
        result.setTotalElements(p.getTotalElements());
        result.setPage(p.getNumber());
        result.setSize(p.getSize());
        return result;
    }

    public Optional<SpeciesResponse> getById(Long id) {
        return speciesRepository.findById(id).map(this::toDto);
    }

    private SpeciesResponse toDto(SpeciesEntity e) {
        SpeciesResponse r = new SpeciesResponse();
        r.setId(e.getId());
        r.setClassId(e.getClassId());
        r.setName(e.getName());
        r.setScientificName(e.getScientificName());
        r.setDescription(e.getDescription());
        return r;
    }
}

