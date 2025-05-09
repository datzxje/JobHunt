package com.jobhunt.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CompanyRequest {
  @NotBlank(message = "Company name must not be empty")
  private String name;

  @NotBlank(message = "Description must not be empty")
  private String description;

  private String websiteUrl;

  @NotBlank(message = "Address must not be empty")
  private String address;

  @Positive(message = "Company size must be positive")
  private Integer companySize;

  @Past(message = "Establishment year must be in the past")
  private Integer establishmentYear;

  @NotBlank(message = "Industry type must not be empty")
  private String industryType;

  @NotBlank(message = "Tax ID must not be empty")
  private String taxId;
}