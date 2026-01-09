package com.pawpplanet.backend.explore.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExploreItemDTO {
    private String type;  // "post", "pet", "user"
    private Object data;  // Will be PostResponse, PetExploreDTO, or UserExploreDTO
}

