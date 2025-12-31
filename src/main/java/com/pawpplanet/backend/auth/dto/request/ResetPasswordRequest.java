package com.pawpplanet.backend.auth.dto.request;

import lombok.Data;

@Data
public class ResetPasswordRequest {
    private String newPassword;
    private LogoutRequest logoutRequest;
}


