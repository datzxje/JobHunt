package com.jobhunt.model.request;

import com.jobhunt.model.entity.RequirementType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class JobRequirementRequest {

  @NotNull(message = "Requirement type is required")
  private RequirementType type;

  @NotNull(message = "Weight is required")
  @Min(value = 1, message = "Weight must be between 1 and 10")
  @Max(value = 10, message = "Weight must be between 1 and 10")
  private Integer weight;

  private Boolean isMandatory = false;

  private String criteriaData; // JSON string containing the specific criteria

  private String description; // Human-readable description
}