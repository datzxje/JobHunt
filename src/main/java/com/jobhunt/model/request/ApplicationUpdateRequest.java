package com.jobhunt.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ApplicationUpdateRequest {
  @NotBlank(message = "Cover letter is required")
  private String coverLetter;

  @Positive(message = "Expected salary must be positive")
  private Double expectedSalary;
}