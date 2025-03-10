package com.jobhunt.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VerificationRequest {
    @NotNull
    @Email
    private String email;

    @NotNull
    private String code;

    @NotNull
    private SignUpRequest signUpRequest;
}
