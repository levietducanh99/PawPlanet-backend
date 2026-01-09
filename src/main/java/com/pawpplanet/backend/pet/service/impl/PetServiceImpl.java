package com.pawpplanet.backend.pet.service.impl;

import com.pawpplanet.backend.encyclopedia.repository.BreedRepository;
import com.pawpplanet.backend.encyclopedia.repository.SpeciesRepository;
import com.pawpplanet.backend.media.service.CloudinaryUrlBuilder;
import com.pawpplanet.backend.pet.dto.*;
import com.pawpplanet.backend.pet.entity.PetEntity;
import com.pawpplanet.backend.pet.entity.PetMediaEntity;
import com.pawpplanet.backend.pet.mapper.PetMapper;
import com.pawpplanet.backend.pet.repository.PetMediaRepository;
import com.pawpplanet.backend.pet.repository.PetRepository;
import com.pawpplanet.backend.pet.service.FollowPetService;
import com.pawpplanet.backend.pet.service.PetService;
import com.pawpplanet.backend.user.repository.UserRepository;
import com.pawpplanet.backend.utils.SecurityHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PetServiceImpl implements PetService {

    private final PetRepository petRepository;
    private final UserRepository userRepository;
    private final SpeciesRepository speciesRepository;
    private final BreedRepository breedRepository;
    private final PetMediaRepository petMediaRepository;
    private final FollowPetService followPetService;
    private final CloudinaryUrlBuilder cloudinaryUrlBuilder;
    private final SecurityHelper securityHelper;

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
        pet.setWeight(request.getWeight());
        pet.setHeight(request.getHeight());

        pet.setOwnerId(user.getId());

        petRepository.save(pet);

        // Xử lý avatar nếu có avatarPublicId
        if (request.getAvatarPublicId() != null && !request.getAvatarPublicId().isBlank()) {
            // Build URL từ publicId
            String avatarUrl = cloudinaryUrlBuilder.buildOptimizedUrl(
                    request.getAvatarPublicId(),
                    "image"
            );


            PetMediaEntity media = new PetMediaEntity();
            media.setPetId(pet.getId());
            media.setType("image");
            media.setRole("avatar");
            media.setPublicId(request.getAvatarPublicId());
            media.setUrl(avatarUrl);
            media.setDisplayOrder(1);
            petMediaRepository.save(media);
        } else {
            // Không có avatar → dùng default hoặc không tạo media
            PetMediaEntity media = new PetMediaEntity();
            media.setPetId(pet.getId());
            media.setType("image");
            media.setRole("avatar");
            media.setUrl("https://res.cloudinary.com/demo/image/upload/v1700000000/default_pet.jpg");
            media.setDisplayOrder(1);
            petMediaRepository.save(media);
        }

        // Lấy media để trả về
        List<PetMediaEntity> mediaList = petMediaRepository.findByPetId(pet.getId());

        // Chỉ thêm phần này
        PetProfileDTO dto = PetMapper.toProfileDTO(pet, mediaList);
        return enrichPetDTO(dto, pet);
    }

    @Override
    public PetProfileDTO getPetById(Long petId) {
        PetEntity pet = petRepository.findById(petId)
                .orElseThrow(() -> new RuntimeException("Pet not found"));

        // Privacy check: if status is "private", only owner can view
        Long currentUserId = securityHelper.getCurrentUserId();
        if ("private".equalsIgnoreCase(pet.getStatus())) {
            if (currentUserId == null || !currentUserId.equals(pet.getOwnerId())) {
                throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "This pet profile is private"
                );
            }
        }

        // GIỮ NGUYÊN PHẦN CŨ CỦA BẠN
        List<PetMediaEntity> media = petMediaRepository.findByPetId(petId);

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
        if (request.getWeight() != null) pet.setWeight(request.getWeight());
        if (request.getHeight() != null) pet.setHeight(request.getHeight());

        petRepository.save(pet);

        // 5️⃣ Update avatar nếu có publicId
        if (request.getAvatarPublicId() != null && !request.getAvatarPublicId().isBlank()) {
            // Archive old avatar(s) — handle unexpected duplicates safely
            List<PetMediaEntity> existingAvatars = petMediaRepository.findByPetIdAndDisplayOrder(petId, 1);
            if (!existingAvatars.isEmpty()) {
                for (PetMediaEntity old : existingAvatars) {
                    old.setDisplayOrder(0);
                }
                petMediaRepository.saveAll(existingAvatars);
            }

            // Build URL and save
            String avatarUrl = cloudinaryUrlBuilder.buildOptimizedUrl(
                    request.getAvatarPublicId(),
                    "image"
            );

            PetMediaEntity newMedia = new PetMediaEntity();
            newMedia.setPetId(petId);
            newMedia.setType("image");
            newMedia.setRole("avatar");
            newMedia.setPublicId(request.getAvatarPublicId());
            newMedia.setUrl(avatarUrl);
            newMedia.setDisplayOrder(1);

            petMediaRepository.save(newMedia);
        }

        // 6️⃣ Lấy media đúng cách (KHÔNG dùng findAll)
        List<PetMediaEntity> mediaList =
                petMediaRepository.findByPetId(petId);

        PetProfileDTO dto = PetMapper.toProfileDTO(pet, mediaList);
        return enrichPetDTO(dto, pet);
    }

    @Override
    public AddPetMediaResponse addMediaToGallery(Long petId, AddPetMediaRequest request) {
        // 1️⃣ Validate pet exists
        PetEntity pet = petRepository.findById(petId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Pet not found"
                        )
                );

        // 2️⃣ Verify ownership
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
                    "You are not allowed to add media to this pet"
            );
        }

        // 3️⃣ Validate request
        if (request.getMediaItems() == null || request.getMediaItems().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Media items cannot be empty"
            );
        }

        // 4️⃣ Get current max display order for gallery items
        List<PetMediaEntity> existingGalleryMedia = petMediaRepository.findByPetId(petId)
                .stream()
                .filter(m -> "gallery".equals(m.getRole()))
                .toList();

        int maxDisplayOrder = existingGalleryMedia.stream()
                .mapToInt(PetMediaEntity::getDisplayOrder)
                .max()
                .orElse(0);

        // 5️⃣ Create and save new media entities
        List<PetMediaEntity> newMediaEntities = new ArrayList<>();
        int currentDisplayOrder = maxDisplayOrder + 1;

        for (AddPetMediaRequest.MediaItem item : request.getMediaItems()) {
            // Build URL from public ID with optimization (builder validates internally)
            String url = cloudinaryUrlBuilder.buildOptimizedUrl(
                    item.getPublicId(),
                    item.getType()
            );

            PetMediaEntity mediaEntity = new PetMediaEntity();
            mediaEntity.setPetId(petId);
            mediaEntity.setType(item.getType());
            mediaEntity.setRole("gallery");
            mediaEntity.setPublicId(item.getPublicId());
            mediaEntity.setUrl(url);
            mediaEntity.setDisplayOrder(currentDisplayOrder++);

            newMediaEntities.add(mediaEntity);
        }

        // Save all media entities
        List<PetMediaEntity> savedMedia = petMediaRepository.saveAll(newMediaEntities);

        // 6️⃣ Build response
        List<PetMediaDTO> addedMediaDTOs = savedMedia.stream()
                .map(PetMapper::toMediaDTO)
                .toList();

        // Get total gallery count
        int totalGalleryCount = (int) petMediaRepository.findByPetId(petId)
                .stream()
                .filter(m -> "gallery".equals(m.getRole()))
                .count();

        return AddPetMediaResponse.builder()
                .petId(petId)
                .addedMedia(addedMediaDTOs)
                .totalGalleryCount(totalGalleryCount)
                .message("Successfully added " + savedMedia.size() + " media item(s) to gallery")
                .build();
    }

    @Override
    public List<AllPetsResponseDTO> getAllMyPets() {
        // 1️⃣ Get current user
        Long currentUserId = securityHelper.getCurrentUserId();

        // 2️⃣ Fetch all pets owned by current user
        List<PetEntity> pets = petRepository.findByOwnerId(currentUserId);

        // 3️⃣ Convert to DTOs with enriched data
        return pets.stream()
                .map(pet -> {
                    List<PetMediaEntity> media = petMediaRepository.findByPetId(pet.getId());
                    AllPetsResponseDTO dto = new AllPetsResponseDTO();
                    dto.setId(pet.getId());
                    dto.setName(pet.getName());
                    dto.setAvatar(getAvatarFromMedia(media)); // Extract avatar URL
                    if (pet.getSpeciesId() != null) {
                        speciesRepository.findById(pet.getSpeciesId())
                                .ifPresent(s -> dto.setSpeciesName(s.getName()));
                    }
                    return dto;
                })
                .toList();

    }

    @Override
    public void deletePet(Long petId) {
        PetEntity pet = petRepository.findById(petId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Pet not found"
                        )
                );

        Long currentUserId = securityHelper.getCurrentUserId();

        if (!pet.getOwnerId().equals(currentUserId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not allowed to delete this pet"
            );
        }

        // Soft delete the pet
        pet.setIsDeleted(true);
        pet.setDeletedAt(LocalDateTime.now());
        pet.setDeletedBy(currentUserId);

        petRepository.save(pet);

        // Soft delete associated media (do NOT delete files from cloud storage)
        List<PetMediaEntity> mediaList = petMediaRepository.findByPetId(petId);
        for (PetMediaEntity media : mediaList) {
            media.setIsDeleted(true);
            media.setDeletedAt(LocalDateTime.now());
            media.setDeletedBy(currentUserId);
        }
        if (!mediaList.isEmpty()) {
            petMediaRepository.saveAll(mediaList);
        }
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
        
        // Computed fields
        Long currentUserId = securityHelper.getCurrentUserId();
        if (currentUserId != null) {
            // isOwner
            dto.setOwner(currentUserId.equals(pet.getOwnerId()));
            
            // isFollowing
            try {
                dto.setFollowing(followPetService.isFollowingPet(pet.getId()));
            } catch (Exception e) {
                dto.setFollowing(false);
            }
            
            // canFollow = not owner ONLY (do NOT consider following status)
            dto.setCanFollow(!dto.isOwner());
        } else {
            // User not authenticated
            dto.setOwner(false);
            dto.setFollowing(false);
            dto.setCanFollow(false);
        }
        
        return dto;
    }

    private String getAvatarFromMedia(List<PetMediaEntity> media) {
        return media.stream()
                .filter(m -> "avatar".equals(m.getRole()))
                .findFirst()
                .map(PetMediaEntity::getUrl)
                .orElse(null);
    }
//
//    private Long getCurrentUserIdOrNull() {
//        try {
//            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//            if (authentication == null || !authentication.isAuthenticated()) {
//                return null;
//            }
//
//            String email = authentication.getName();
//            if (email == null || "anonymousUser".equals(email)) {
//                return null;
//            }
//
//            return userRepository.findByEmail(email)
//                    .map(u -> u.getId())
//                    .orElse(null);
//        } catch (Exception e) {
//            return null;
//        }
//    }
    
    private void validateSpeciesAndBreed(Long speciesId, Long breedId) {
        if (speciesId != null && !speciesRepository.existsById(speciesId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Loài (Species) không tồn tại");
        }
        if (breedId != null && !breedRepository.existsById(breedId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Giống (Breed) không tồn tại");
        }
    }
}

