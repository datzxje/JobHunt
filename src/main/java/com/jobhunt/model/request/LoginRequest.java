package com.jobhunt.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "Username must not be empty")
    private String email;

    @NotBlank(message = "Password must not be empty")
    private String password;
}
