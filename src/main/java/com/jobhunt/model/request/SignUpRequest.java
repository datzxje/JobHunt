package com.jobhunt.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignUpRequest {

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Username must not be empty")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;

    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;

    @NotBlank(message = "Phone number must not be empty")
    @Pattern(regexp = "^(09|03)\\d{8}$", message = "Phone number must be valid")
    private String phoneNumber;

    private String profilePictureUrl;

    @NotBlank(message = "Role is required")
    @Pattern(regexp = "^(EMPLOYER|CANDIDATE)$", message = "Role must be either EMPLOYER or CANDIDATE")
    private String role;

}
