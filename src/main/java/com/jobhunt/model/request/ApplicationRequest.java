package com.jobhunt.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ApplicationRequest {
  @NotNull(message = "Job ID is required")
  private Long jobId;

  @NotBlank(message = "Cover letter is required")
  private String coverLetter;

  @NotNull(message = "Expected salary is required")
  @Positive(message = "Expected salary must be positive")
  private Double expectedSalary;

  @NotBlank(message = "Candidate profile is required")
  private String candidateProfile; // JSON string containing all candidate requirements data
}