package com.jobhunt.model.request;

import com.jobhunt.model.entity.Job.EmploymentType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class JobRequest {
  @NotBlank(message = "Title must not be empty")
  private String title;

  @NotBlank(message = "Description must not be empty")
  private String description;

  @NotBlank(message = "Requirements must not be empty")
  private String requirements;

  @Positive(message = "Minimum salary must be positive")
  private BigDecimal salaryMin;

  @Positive(message = "Maximum salary must be positive")
  private BigDecimal salaryMax;

  @NotNull(message = "Employment type must not be empty")
  private EmploymentType employmentType;

  @NotBlank(message = "Experience level must not be empty")
  private String experienceLevel;

  @NotBlank(message = "Location must not be empty")
  private String location;

  private boolean isRemote;

  @Future(message = "Application deadline must be in the future")
  private LocalDateTime applicationDeadline;
}