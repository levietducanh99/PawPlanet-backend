package com.pawpplanet.backend.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class ApiResponse <T>{

    private T result;
    private String message;
    private Integer statusCode;

}
