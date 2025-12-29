package com.pawpplanet.backend.auth.dto.request;

import lombok.Data;

@Data

public class LogoutRequest {
    private String token;
}
