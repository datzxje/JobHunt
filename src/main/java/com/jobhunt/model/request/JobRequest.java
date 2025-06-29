package com.jobhunt.model.request;

import com.jobhunt.model.entity.Job.EmploymentType;
import com.jobhunt.model.entity.Job.GenderPreference;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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

  private String careerLevel;

  @NotBlank(message = "Location must not be empty")
  private String location;

  private String country;

  private String city;

  private String address;

  private boolean isRemote;

  @Future(message = "Application deadline must be in the future")
  private LocalDateTime applicationDeadline;

  // New fields from frontend form
  private String hoursPerWeek;

  private GenderPreference genderPreference;

  private String minimumQualification;

  private Integer minimumAge;

  private Integer maximumAge;

  private Integer minimumExperienceYears;

  private Integer maximumExperienceYears;

  // JSON string fields for names instead of IDs
  private List<String> categories; // ["Technology", "Software Development"]

  private List<String> requiredSkills; // ["Java", "Spring Boot", "React"]

  private List<String> requiredLanguages; // ["English", "Vietnamese"]

  private String jobRequirements; // JSON string of requirements array
}