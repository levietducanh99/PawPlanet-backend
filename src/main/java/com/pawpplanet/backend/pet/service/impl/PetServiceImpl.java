package com.pawpplanet.backend.pet.service.impl;

import com.pawpplanet.backend.encyclopedia.repository.BreedRepository;
import com.pawpplanet.backend.encyclopedia.repository.SpeciesRepository;
import com.pawpplanet.backend.pet.dto.CreatePetRequestDTO;
import com.pawpplanet.backend.pet.dto.PetProfileDTO;
import com.pawpplanet.backend.pet.dto.UpdatePetRequestDTO;
import com.pawpplanet.backend.pet.entity.PetEntity;
import com.pawpplanet.backend.pet.mapper.PetMapper;
import com.pawpplanet.backend.pet.repository.PetRepository;
import com.pawpplanet.backend.pet.service.PetService;
import com.pawpplanet.backend.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class PetServiceImpl implements PetService {

    private final PetRepository petRepository;
    private final UserRepository userRepository;
    private final SpeciesRepository speciesRepository;
    private final BreedRepository breedRepository;

    public PetServiceImpl(PetRepository petRepository,
                          UserRepository userRepository,
                          SpeciesRepository speciesRepository,
                          BreedRepository breedRepository) {
        this.petRepository = petRepository;
        this.userRepository = userRepository;
        this.speciesRepository = speciesRepository;
        this.breedRepository = breedRepository;
    }

    @Override
    public PetProfileDTO createPet(CreatePetRequestDTO request) {
        // 1. Validate sự tồn tại của Species và Breed trong Encyclopedia
        if (request.getSpeciesId() != null && !speciesRepository.existsById(request.getSpeciesId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Loài (Species) không tồn tại trong hệ thống");
        }
        if (request.getBreedId() != null && !breedRepository.existsById(request.getBreedId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Giống (Breed) không tồn tại trong hệ thống");
        }

        PetEntity toSave = PetMapper.toEntity(request);

        // Giả lập lấy user từ context (Sau này thay bằng SecurityContext/JWT)
        String email = "user1@example.com";
        userRepository.findByEmail(email).ifPresent(user -> toSave.setOwnerId(user.getId()));

        PetEntity saved = petRepository.save(toSave);

        // 2. Map dữ liệu sang DTO và chèn thông tin tên từ Encyclopedia
        PetProfileDTO dto = PetMapper.toDto(saved);

        // Lấy tên loài từ SpeciesEntity
        if (saved.getSpeciesId() != null) {
            speciesRepository.findById(saved.getSpeciesId())
                    .ifPresent(s -> dto.setSpeciesName(s.getName()));
        }

        // Lấy tên giống từ BreedEntity
        if (saved.getBreedId() != null) {
            breedRepository.findById(saved.getBreedId())
                    .ifPresent(b -> dto.setBreedName(b.getName()));
        }

        // Lấy thông tin chủ sở hữu
        if (saved.getOwnerId() != null) {
            userRepository.findById(saved.getOwnerId())
                    .ifPresent(u -> dto.setOwnerUsername(u.getUsername()));
        }

        return dto;
    }

    @Override
    public PetProfileDTO updatePet(Long id, UpdatePetRequestDTO dto) {
        PetEntity pet = petRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pet not found"));

        // Kiểm tra logic nếu cập nhật Species/Breed mới
        if (dto.getSpeciesId() != null && !speciesRepository.existsById(dto.getSpeciesId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Species ID không hợp lệ");
        }
        if (dto.getBreedId() != null && !breedRepository.existsById(dto.getBreedId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Breed ID không hợp lệ");
        }

        // Update fields
        if (dto.getName() != null) pet.setName(dto.getName());
        if (dto.getSpeciesId() != null) pet.setSpeciesId(dto.getSpeciesId());
        if (dto.getBreedId() != null) pet.setBreedId(dto.getBreedId());
        if (dto.getBirthDate() != null) pet.setBirthDate(dto.getBirthDate());
        if (dto.getGender() != null) pet.setGender(dto.getGender());
        if (dto.getDescription() != null) pet.setDescription(dto.getDescription());
        if (dto.getStatus() != null) pet.setStatus(dto.getStatus());

        PetEntity saved = petRepository.save(pet);

        // Trả về DTO kèm theo tên hiển thị
        PetProfileDTO resultDto = PetMapper.toDto(saved);

        if (saved.getSpeciesId() != null) {
            speciesRepository.findById(saved.getSpeciesId())
                    .ifPresent(s -> resultDto.setSpeciesName(s.getName()));
        }
        if (saved.getBreedId() != null) {
            breedRepository.findById(saved.getBreedId())
                    .ifPresent(b -> resultDto.setBreedName(b.getName()));
        }
        if (saved.getOwnerId() != null) {
            userRepository.findById(saved.getOwnerId())
                    .ifPresent(u -> resultDto.setOwnerUsername(u.getUsername()));
        }

        return resultDto;
    }
}