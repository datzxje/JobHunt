package com.jobhunt.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CompanyRequest {
  // Company Profile Section
  private String logoUrl;
  private String coverUrl;

  @NotBlank(message = "Company name is required")
  private String name;

  @NotBlank(message = "Email is required")
  @Email(message = "Invalid email format")
  private String email;

  private String phoneNumber;
  private String websiteUrl;

  @NotNull(message = "Establishment year is required")
  @Max(value = 2025, message = "Establishment year cannot be in the future")
  private Integer establishmentYear;

  @NotNull(message = "Team size is required")
  @Positive(message = "Team size must be positive")
  private String teamSize;

  @NotBlank(message = "Industry type is required")
  private String industryType;

  private String about;

  // Social Network Section
  @Pattern(regexp = "^(https?://)?(www\\.)?facebook\\.com/.*$", message = "Invalid Facebook URL")
  private String facebookUrl;

  @Pattern(regexp = "^(https?://)?(www\\.)?twitter\\.com/.*$", message = "Invalid Twitter URL")
  private String twitterUrl;

  @Pattern(regexp = "^(https?://)?(www\\.)?linkedin\\.com/.*$", message = "Invalid LinkedIn URL")
  private String linkedinUrl;

  @Pattern(regexp = "^(https?://)?(www\\.)?plus\\.google\\.com/.*$", message = "Invalid Google Plus URL")
  private String googlePlusUrl;

  @Pattern(regexp = "^(https?://)?(www\\.)?instagram\\.com/.*$", message = "Invalid Instagram URL")
  private String socialInstagram;

  // Contact Information Section
  @NotBlank(message = "Country is required")
  private String country;

  @NotBlank(message = "City is required")
  private String city;

  @NotBlank(message = "Address is required")
  private String address;

  private Double latitude;

  private Double longitude;

  @NotBlank(message = "Tax ID is required")
  private String taxId;
}