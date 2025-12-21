package com.pawpplanet.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Pet information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Pet information details")
public class PetDto {

    @Schema(description = "Unique identifier of the pet", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotBlank(message = "Pet name is required")
    @Size(min = 1, max = 100, message = "Pet name must be between 1 and 100 characters")
    @Schema(description = "Name of the pet", example = "Buddy", required = true)
    private String name;

    @NotBlank(message = "Pet species is required")
    @Schema(description = "Species of the pet", example = "Dog", required = true)
    private String species;

    @Schema(description = "Breed of the pet", example = "Golden Retriever")
    private String breed;

    @Schema(description = "Age of the pet in years", example = "3")
    private Integer age;

    @Schema(description = "Description or notes about the pet", example = "Friendly and playful")
    private String description;
}
