package com.pawpplanet.backend.controller;

import com.pawpplanet.backend.dto.ApiResponse;
import com.pawpplanet.backend.dto.PetDto;
import com.pawpplanet.backend.service.PetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing Pet resources.
 * Provides CRUD operations for pets with comprehensive Swagger documentation.
 */
@RestController
@RequestMapping("/api/v1/pets")
@Tag(name = "Pet Management", description = "APIs for managing pet information")
public class PetController {

    private final PetService petService;

    public PetController(PetService petService) {
        this.petService = petService;
    }

    /**
     * Retrieves all pets.
     *
     * @return List of all pets
     */
    @Operation(
            summary = "Get all pets",
            description = "Retrieves a list of all pets in the system"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved list of pets",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    @GetMapping
    public ResponseEntity<ApiResponse<List<PetDto>>> getAllPets() {
        List<PetDto> pets = petService.getAllPets();
        return ResponseEntity.ok(
                ApiResponse.success("Pets retrieved successfully", pets)
        );
    }

    /**
     * Retrieves a specific pet by ID.
     *
     * @param id Pet ID
     * @return Pet details
     */
    @Operation(
            summary = "Get pet by ID",
            description = "Retrieves detailed information about a specific pet"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Pet found and returned successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Pet not found",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PetDto>> getPetById(
            @Parameter(description = "ID of the pet to retrieve", required = true, example = "1")
            @PathVariable Long id) {
        PetDto pet = petService.getPetById(id);
        if (pet == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Pet not found with id: " + id));
        }
        return ResponseEntity.ok(
                ApiResponse.success("Pet retrieved successfully", pet)
        );
    }

    /**
     * Creates a new pet.
     *
     * @param petDto Pet data
     * @return Created pet
     */
    @Operation(
            summary = "Create a new pet",
            description = "Creates a new pet with the provided information"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "Pet created successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    @PostMapping
    public ResponseEntity<ApiResponse<PetDto>> createPet(
            @Parameter(description = "Pet information to create", required = true)
            @Valid @RequestBody PetDto petDto) {
        PetDto createdPet = petService.createPet(petDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Pet created successfully", createdPet));
    }

    /**
     * Updates an existing pet.
     *
     * @param id     Pet ID
     * @param petDto Updated pet data
     * @return Updated pet
     */
    @Operation(
            summary = "Update a pet",
            description = "Updates an existing pet with the provided information"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Pet updated successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Pet not found",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PetDto>> updatePet(
            @Parameter(description = "ID of the pet to update", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "Updated pet information", required = true)
            @Valid @RequestBody PetDto petDto) {
        PetDto updatedPet = petService.updatePet(id, petDto);
        if (updatedPet == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Pet not found with id: " + id));
        }
        return ResponseEntity.ok(
                ApiResponse.success("Pet updated successfully", updatedPet)
        );
    }

    /**
     * Deletes a pet by ID.
     *
     * @param id Pet ID
     * @return Deletion confirmation
     */
    @Operation(
            summary = "Delete a pet",
            description = "Deletes a pet from the system"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Pet deleted successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Pet not found",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePet(
            @Parameter(description = "ID of the pet to delete", required = true, example = "1")
            @PathVariable Long id) {
        boolean deleted = petService.deletePet(id);
        if (!deleted) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Pet not found with id: " + id));
        }
        return ResponseEntity.ok(
                ApiResponse.success("Pet deleted successfully")
        );
    }
}
