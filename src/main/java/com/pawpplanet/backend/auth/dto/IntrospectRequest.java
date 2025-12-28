package com.pawpplanet.backend.auth.dto;


import lombok.Data;

@Data
public class IntrospectRequest {
    private String token;
}
