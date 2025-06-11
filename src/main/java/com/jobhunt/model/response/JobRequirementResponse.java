package com.jobhunt.model.response;

import com.jobhunt.model.entity.RequirementType;
import lombok.Data;

import java.time.Instant;

@Data
public class JobRequirementResponse {
  private Long id;
  private Long jobId;
  private RequirementType type;
  private Integer weight;
  private Boolean isMandatory;
  private String criteriaData;
  private String description;
  private Instant createdAt;
  private Instant updatedAt;
}