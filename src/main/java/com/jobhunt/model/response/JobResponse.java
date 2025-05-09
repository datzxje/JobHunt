package com.jobhunt.model.response;

import com.jobhunt.model.entity.Job.EmploymentType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

@Data
public class JobResponse {
  private Long id;
  private String title;
  private String description;
  private String requirements;
  private BigDecimal salaryMin;
  private BigDecimal salaryMax;
  private EmploymentType employmentType;
  private String experienceLevel;
  private String location;
  private boolean isRemote;
  private LocalDateTime applicationDeadline;
  private boolean active;
  private CompanyResponse company;
  private Instant createdAt;
  private Instant updatedAt;
  private long numberOfApplications;
}