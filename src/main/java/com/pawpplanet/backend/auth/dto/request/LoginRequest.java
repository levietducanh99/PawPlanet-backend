package com.pawpplanet.backend.auth.dto.request;


import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
    @Email(message = "Invalid email format")
    private String email;
    private String password;
}
