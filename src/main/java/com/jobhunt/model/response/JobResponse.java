package com.jobhunt.model.response;

import com.jobhunt.model.entity.Job.EmploymentType;
import com.jobhunt.model.entity.Job.GenderPreference;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Set;

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
  private String careerLevel;
  private String location;
  private String country;
  private String city;
  private String address;
  private boolean isRemote;
  private LocalDateTime applicationDeadline;
  private String hoursPerWeek;
  private GenderPreference genderPreference;
  private String minimumQualification;
  private Integer minimumAge;
  private Integer maximumAge;
  private Integer minimumExperienceYears;
  private Integer maximumExperienceYears;
  private boolean active;
  private CompanyResponse company;
  private Instant createdAt;
  private Instant updatedAt;
  private long numberOfApplications;

  // Relationship data
  private Set<JobCategoryResponse> categories;
  private Set<SkillResponse> requiredSkills;
  private Set<LanguageResponse> requiredLanguages;
  private Set<JobRequirementResponse> jobRequirements;
}