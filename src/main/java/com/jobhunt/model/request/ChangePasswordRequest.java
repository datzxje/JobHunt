package com.jobhunt.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChangePasswordRequest {
  @NotBlank(message = "Current password must not be empty")
  private String currentPassword;

  @NotBlank(message = "New password must not be empty")
  private String newPassword;

  @NotBlank(message = "Confirm password must not be empty")
  private String confirmPassword;
}