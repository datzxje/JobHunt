package com.jobhunt.model.response;

import com.jobhunt.model.entity.ServiceJob;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ServiceJobResponse {
  private Long id;
  private String title;
  private String description;
  private ServiceJob.ServiceType serviceType;
  private String location;
  private BigDecimal estimatedBudget;
  private LocalDateTime requiredCompletionDate;
  private boolean isUrgent;
  private boolean active;
  private Long postedByUserId;
  private String postedByUserName;
  private Long assignedToUserId;
  private String assignedToUserName;
  private ServiceJob.JobStatus status;
  private int applicationsCount;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}