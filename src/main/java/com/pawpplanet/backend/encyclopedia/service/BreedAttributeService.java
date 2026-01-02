package com.pawpplanet.backend.encyclopedia.service;

import com.pawpplanet.backend.common.dto.PagedResult;
import com.pawpplanet.backend.encyclopedia.dto.BreedAttributeResponse;
import com.pawpplanet.backend.encyclopedia.entity.BreedAttributeEntity;
import com.pawpplanet.backend.encyclopedia.repository.BreedAttributeRepository;
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
public class BreedAttributeService {

    private final BreedAttributeRepository repository;

    public PagedResult<BreedAttributeResponse> getAttributes(Long breedId, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(0, page), Math.max(1, size));
        Page<BreedAttributeEntity> p = repository.findByBreedId(breedId, pageable);

        PagedResult<BreedAttributeResponse> r = new PagedResult<>();
        r.setItems(p.getContent().stream().map(this::toDto).collect(Collectors.toList()));
        r.setTotalElements(p.getTotalElements());
        r.setPage(p.getNumber());
        r.setSize(p.getSize());
        return r;
    }

    public List<BreedAttributeResponse> getAllAttributes(Long breedId) {
        return repository.findByBreedIdOrderByDisplayOrderAsc(breedId).stream().map(this::toDto).collect(Collectors.toList());
    }

    public Optional<BreedAttributeResponse> getById(Long id) {
        return repository.findById(id).map(this::toDto);
    }

    private BreedAttributeResponse toDto(BreedAttributeEntity e) {
        BreedAttributeResponse r = new BreedAttributeResponse();
        r.setId(e.getId());
        r.setBreedId(e.getBreedId());
        r.setKey(e.getKey());
        r.setValue(e.getValue());
        r.setDisplayOrder(e.getDisplayOrder());
        return r;
    }
}

