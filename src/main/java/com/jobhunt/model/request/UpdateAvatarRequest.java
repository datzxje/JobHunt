package com.jobhunt.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateAvatarRequest {
  @NotBlank(message = "Avatar URL must not be empty")
  private String avatarUrl;
}