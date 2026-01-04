package com.pawpplanet.backend.encyclopedia.service;

import com.pawpplanet.backend.encyclopedia.dto.SearchResponse;
import com.pawpplanet.backend.encyclopedia.dto.SearchResultItem;
import com.pawpplanet.backend.encyclopedia.entity.AnimalClassEntity;
import com.pawpplanet.backend.encyclopedia.entity.BreedEntity;
import com.pawpplanet.backend.encyclopedia.entity.SpeciesEntity;
import com.pawpplanet.backend.encyclopedia.repository.AnimalClassRepository;
import com.pawpplanet.backend.encyclopedia.repository.BreedRepository;
import com.pawpplanet.backend.encyclopedia.repository.SpeciesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final AnimalClassRepository classRepository;
    private final SpeciesRepository speciesRepository;
    private final BreedRepository breedRepository;
    private final EncyclopediaMediaService mediaService;

    public SearchResponse searchAll(String q) {
        String keyword = q == null ? "" : q.trim();
        SearchResponse r = new SearchResponse();
        List<SearchResultItem> items = new ArrayList<>();
        Set<String> seen = new HashSet<>();

        // Search classes by name or code
        List<AnimalClassEntity> classes = classRepository.findAll().stream()
                .filter(c -> containsIgnoreCase(c.getName(), keyword) || containsIgnoreCase(c.getCode(), keyword) || keyword.isEmpty())
                .collect(Collectors.toList());

        for (AnimalClassEntity c : classes) {
            String key = "class:" + c.getId();
            if (seen.add(key)) {
                SearchResultItem it = new SearchResultItem();
                it.setType("class");
                it.setId(c.getId());
                it.setName(c.getName());
                it.setSlug(c.getCode());
                it.setSubtitle(null);
                it.setAvatarUrl(mediaService.getThumbnailUrl("class", c.getId()));
                items.add(it);
            }

            // add species under class
            List<SpeciesEntity> speciesUnder = speciesRepository.findByClassId(c.getId());
            for (SpeciesEntity s : speciesUnder) {
                String skey = "species:" + s.getId();
                if (seen.add(skey)) {
                    SearchResultItem sit = new SearchResultItem();
                    sit.setType("species");
                    sit.setId(s.getId());
                    sit.setName(s.getName());
                    sit.setSlug(s.getSlug());
                    sit.setSubtitle(s.getScientificName());
                    sit.setAvatarUrl(mediaService.getThumbnailUrl("species", s.getId()));
                    items.add(sit);
                }

                // add breeds under species
                List<BreedEntity> breeds = breedRepository.findBySpeciesId(s.getId());
                for (BreedEntity b : breeds) {
                    String bkey = "breed:" + b.getId();
                    if (seen.add(bkey)) {
                        SearchResultItem bit = new SearchResultItem();
                        bit.setType("breed");
                        bit.setId(b.getId());
                        bit.setName(b.getName());
                        bit.setSlug(b.getSlug() != null ? b.getSlug() : b.getName().toLowerCase().replaceAll("\\s+", "-"));
                        bit.setSubtitle(null);
                        bit.setAvatarUrl(mediaService.getThumbnailUrl("breed", b.getId()));
                        items.add(bit);
                    }
                }
            }
        }

        // Species direct matches
        List<SpeciesEntity> speciesMatches = speciesRepository.findByNameContainingIgnoreCaseOrScientificNameContainingIgnoreCase(keyword, keyword);
        for (SpeciesEntity s : speciesMatches) {
            String skey = "species:" + s.getId();
            if (seen.add(skey)) {
                SearchResultItem sit = new SearchResultItem();
                sit.setType("species");
                sit.setId(s.getId());
                sit.setName(s.getName());
                sit.setSlug(s.getSlug());
                sit.setSubtitle(s.getScientificName());
                sit.setAvatarUrl(mediaService.getThumbnailUrl("species", s.getId()));
                items.add(sit);
            }

            // add breeds under matched species
            List<BreedEntity> breeds = breedRepository.findBySpeciesId(s.getId());
            for (BreedEntity b : breeds) {
                String bkey = "breed:" + b.getId();
                if (seen.add(bkey)) {
                    SearchResultItem bit = new SearchResultItem();
                    bit.setType("breed");
                    bit.setId(b.getId());
                    bit.setName(b.getName());
                    bit.setSlug(b.getSlug() != null ? b.getSlug() : b.getName().toLowerCase().replaceAll("\\s+", "-"));
                    bit.setSubtitle(null);
                    bit.setAvatarUrl(mediaService.getThumbnailUrl("breed", b.getId()));
                    items.add(bit);
                }
            }
        }

        // Breed direct matches
        List<BreedEntity> breedMatches = breedRepository.findByNameContainingIgnoreCase(keyword);
        for (BreedEntity b : breedMatches) {
            String bkey = "breed:" + b.getId();
            if (seen.add(bkey)) {
                SearchResultItem bit = new SearchResultItem();
                bit.setType("breed");
                bit.setId(b.getId());
                bit.setName(b.getName());
                bit.setSlug(b.getSlug() != null ? b.getSlug() : b.getName().toLowerCase().replaceAll("\\s+", "-"));
                bit.setSubtitle(null);
                bit.setAvatarUrl(mediaService.getThumbnailUrl("breed", b.getId()));
                items.add(bit);
            }
        }

        r.setItems(items);
        return r;
    }

    private boolean containsIgnoreCase(String source, String sub) {
        if (source == null || sub == null) return false;
        return source.toLowerCase().contains(sub.toLowerCase());
    }
}

