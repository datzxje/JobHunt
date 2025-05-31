package com.jobhunt.model.request;

import com.jobhunt.model.entity.ServiceJob;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ServiceJobRequest {
  @NotBlank(message = "Title is required")
  private String title;

  @NotBlank(message = "Description is required")
  private String description;

  @NotNull(message = "Service type is required")
  private ServiceJob.ServiceType serviceType;

  @NotBlank(message = "Location is required")
  private String location;

  @DecimalMin(value = "0.0", inclusive = false, message = "Budget must be greater than 0")
  private BigDecimal estimatedBudget;

  private LocalDateTime requiredCompletionDate;

  private boolean isUrgent;

  private boolean active = true;
}