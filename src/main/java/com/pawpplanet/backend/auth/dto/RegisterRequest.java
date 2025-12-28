package com.pawpplanet.backend.auth.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)

public class RegisterRequest {
    @NonNull
    @Size(min =3 , max = 30, message = "Username must be between 3 and 30 characters long")
    private String username;
    @NonNull
    @Email(message = "Invalid email format")
    private String email;
    @NonNull
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;
    private String avatarUrl;
    private String bio;
}
