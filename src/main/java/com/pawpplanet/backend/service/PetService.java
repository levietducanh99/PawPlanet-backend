package com.pawpplanet.backend.service;

import com.pawpplanet.backend.dto.PetDto;
import com.pawpplanet.backend.model.Pet;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Service layer for Pet operations.
 * Manages business logic for pet-related operations.
 */
@Service
public class PetService {

    private final Map<Long, Pet> petRepository = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    /**
     * Retrieves all pets.
     *
     * @return List of all pets
     */
    public List<PetDto> getAllPets() {
        return petRepository.values().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a pet by ID.
     *
     * @param id Pet ID
     * @return PetDto if found, null otherwise
     */
    public PetDto getPetById(Long id) {
        Pet pet = petRepository.get(id);
        return pet != null ? convertToDto(pet) : null;
    }

    /**
     * Creates a new pet.
     *
     * @param petDto Pet data
     * @return Created pet with generated ID
     */
    public PetDto createPet(PetDto petDto) {
        Pet pet = convertToEntity(petDto);
        pet.setId(idGenerator.getAndIncrement());
        petRepository.put(pet.getId(), pet);
        return convertToDto(pet);
    }

    /**
     * Updates an existing pet.
     *
     * @param id     Pet ID to update
     * @param petDto Updated pet data
     * @return Updated pet if found, null otherwise
     */
    public PetDto updatePet(Long id, PetDto petDto) {
        if (!petRepository.containsKey(id)) {
            return null;
        }
        Pet pet = convertToEntity(petDto);
        pet.setId(id);
        petRepository.put(id, pet);
        return convertToDto(pet);
    }

    /**
     * Deletes a pet by ID.
     *
     * @param id Pet ID to delete
     * @return true if deleted, false if not found
     */
    public boolean deletePet(Long id) {
        return petRepository.remove(id) != null;
    }

    /**
     * Converts Pet entity to PetDto.
     *
     * @param pet Pet entity
     * @return PetDto
     */
    private PetDto convertToDto(Pet pet) {
        return PetDto.builder()
                .id(pet.getId())
                .name(pet.getName())
                .species(pet.getSpecies())
                .breed(pet.getBreed())
                .age(pet.getAge())
                .description(pet.getDescription())
                .build();
    }

    /**
     * Converts PetDto to Pet entity.
     *
     * @param petDto PetDto
     * @return Pet entity
     */
    private Pet convertToEntity(PetDto petDto) {
        return Pet.builder()
                .id(petDto.getId())
                .name(petDto.getName())
                .species(petDto.getSpecies())
                .breed(petDto.getBreed())
                .age(petDto.getAge())
                .description(petDto.getDescription())
                .build();
    }
}
