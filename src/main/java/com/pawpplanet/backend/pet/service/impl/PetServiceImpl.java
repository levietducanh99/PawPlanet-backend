package com.pawpplanet.backend.pet.service.impl;

import com.pawpplanet.backend.encyclopedia.repository.BreedRepository;
import com.pawpplanet.backend.encyclopedia.repository.SpeciesRepository;
import com.pawpplanet.backend.pet.dto.CreatePetRequestDTO;
import com.pawpplanet.backend.pet.dto.PetProfileDTO;
import com.pawpplanet.backend.pet.dto.UpdatePetRequestDTO;
import com.pawpplanet.backend.pet.entity.PetEntity;
import com.pawpplanet.backend.pet.entity.PetMediaEntity;
import com.pawpplanet.backend.pet.mapper.PetMapper;
import com.pawpplanet.backend.pet.repository.PetMediaRepository;
import com.pawpplanet.backend.pet.repository.PetRepository;
import com.pawpplanet.backend.pet.service.PetService;
import com.pawpplanet.backend.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class PetServiceImpl implements PetService {

    private final PetRepository petRepository;
    private final UserRepository userRepository;
    private final SpeciesRepository speciesRepository;
    private final BreedRepository breedRepository;
    private final PetMediaRepository petMediaRepository;

    public PetServiceImpl(PetRepository petRepository,
                          UserRepository userRepository,
                          SpeciesRepository speciesRepository,
                          BreedRepository breedRepository,
                          PetMediaRepository petMediaRepository) {
        this.petRepository = petRepository;
        this.userRepository = userRepository;
        this.speciesRepository = speciesRepository;
        this.breedRepository = breedRepository;
        this.petMediaRepository = petMediaRepository;
    }

    @Override
    public PetProfileDTO createPet(CreatePetRequestDTO request) {

        validateSpeciesAndBreed(request.getSpeciesId(), request.getBreedId());

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User chưa đăng nhập");
        }

        String email = authentication.getName();

        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy user"
                ));
        // ... Giữ nguyên phần validate và lưu pet của bạn ...
        PetEntity pet = new PetEntity();
        pet.setName(request.getName());
        pet.setSpeciesId(request.getSpeciesId());
        pet.setBreedId(request.getBreedId());
        pet.setBirthDate(request.getBirthDate());
        pet.setGender(request.getGender());
        pet.setDescription(request.getDescription());
        pet.setStatus(request.getStatus());

        pet.setOwnerId(user.getId());

        petRepository.save(pet);

        PetMediaEntity media = new PetMediaEntity();
        media.setPetId(pet.getId());
        media.setType("image");
        media.setRole("avatar");
        media.setUrl("https://res.cloudinary.com/demo/image/upload/v1700000000/default_pet.jpg");
        media.setDisplayOrder(1);
        petMediaRepository.save(media);

        // Chỉ thêm phần này
        PetProfileDTO dto = PetMapper.toProfileDTO(pet, List.of(media));
        return enrichPetDTO(dto, pet);
    }

    @Override
    public PetProfileDTO getPetById(Long petId) {
        PetEntity pet = petRepository.findById(petId)
                .orElseThrow(() -> new RuntimeException("Pet not found"));

        // GIỮ NGUYÊN PHẦN CŨ CỦA BẠN
        List<PetMediaEntity> media =
                petMediaRepository.findAll()
                        .stream()
                        .filter(m -> m.getPetId().equals(petId))
                        .toList();

        // CHỈ THÊM PHẦN NÀY
        PetProfileDTO dto = PetMapper.toProfileDTO(pet, media);
        return enrichPetDTO(dto, pet);
    }

    @Override
    public PetProfileDTO updatePet(Long petId, UpdatePetRequestDTO request) {

        // 1️⃣ Tìm pet (KHÔNG dùng RuntimeException)
        PetEntity pet = petRepository.findById(petId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Pet not found"
                        )
                );

        // 2️⃣ Kiểm tra quyền sở hữu
        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        Long currentUserId = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.UNAUTHORIZED,
                                "User not found"
                        )
                )
                .getId();

        if (!pet.getOwnerId().equals(currentUserId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not allowed to update this pet"
            );
        }

        // 3️⃣ Validate species & breed
        validateSpeciesAndBreed(request.getSpeciesId(), request.getBreedId());

        // 4️⃣ Update pet info
        if (request.getName() != null) pet.setName(request.getName());
        if (request.getSpeciesId() != null) pet.setSpeciesId(request.getSpeciesId());
        if (request.getBreedId() != null) pet.setBreedId(request.getBreedId());
        if (request.getBirthDate() != null) pet.setBirthDate(request.getBirthDate());
        if (request.getGender() != null) pet.setGender(request.getGender());
        if (request.getDescription() != null) pet.setDescription(request.getDescription());
        if (request.getStatus() != null) pet.setStatus(request.getStatus());

        petRepository.save(pet);

        // 5️⃣ Update avatar nếu có
        if (request.getUrl() != null && !request.getUrl().isBlank()) {

            petMediaRepository
                    .findByPetIdAndDisplayOrder(petId, 1)
                    .ifPresent(old -> {
                        old.setDisplayOrder(0);
                        petMediaRepository.save(old);
                    });

            PetMediaEntity newMedia = new PetMediaEntity();
            newMedia.setPetId(petId);
            newMedia.setType("image");
            newMedia.setRole("avatar");
            newMedia.setUrl(request.getUrl());
            newMedia.setDisplayOrder(1);

            petMediaRepository.save(newMedia);
        }

        // 6️⃣ Lấy media đúng cách (KHÔNG dùng findAll)
        List<PetMediaEntity> mediaList =
                petMediaRepository.findByPetId(petId);

        PetProfileDTO dto = PetMapper.toProfileDTO(pet, mediaList);
        return enrichPetDTO(dto, pet);
    }


    // HÀM MỚI THÊM VÀO
    private PetProfileDTO enrichPetDTO(PetProfileDTO dto, PetEntity pet) {
        if (pet.getSpeciesId() != null) {
            speciesRepository.findById(pet.getSpeciesId())
                    .ifPresent(s -> dto.setSpeciesName(s.getName()));
        }
        if (pet.getBreedId() != null) {
            breedRepository.findById(pet.getBreedId())
                    .ifPresent(b -> dto.setBreedName(b.getName()));
        }
        if (pet.getOwnerId() != null) {
            userRepository.findById(pet.getOwnerId())
                    .ifPresent(u -> dto.setOwnerUsername(u.getUsername()));
        }
        return dto;
    }
    private void validateSpeciesAndBreed(Long speciesId, Long breedId) {
        if (speciesId != null && !speciesRepository.existsById(speciesId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Loài (Species) không tồn tại");
        }
        if (breedId != null && !breedRepository.existsById(breedId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Giống (Breed) không tồn tại");
        }
    }
}