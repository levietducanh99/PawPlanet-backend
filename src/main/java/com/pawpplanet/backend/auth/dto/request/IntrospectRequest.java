package com.pawpplanet.backend.auth.dto.request;


import lombok.Data;

@Data
public class IntrospectRequest {
    private String token;
}
