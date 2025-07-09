package com.jobhunt.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CompanyJoinRequestRequest {

  @NotNull(message = "Company ID is required")
  private Long companyId;

  @NotBlank(message = "Message is required")
  @Size(min = 10, max = 1000, message = "Message must be between 10 and 1000 characters")
  private String message;
}