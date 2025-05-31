package com.jobhunt.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CompanyAdminSetupRequest {

  // Company Information
  @NotBlank(message = "Company name is required")
  private String companyName;

  @NotBlank(message = "Company email is required")
  @Email(message = "Invalid company email format")
  private String companyEmail;

  private String companyPhone;
  private String companyWebsite;

  @NotNull(message = "Establishment year is required")
  @Max(value = 2025, message = "Establishment year cannot be in the future")
  private Integer establishmentYear;

  @NotBlank(message = "Team size is required")
  private String teamSize;

  @NotBlank(message = "Industry type is required")
  private String industryType;

  private String companyAbout;

  @NotBlank(message = "Country is required")
  private String country;

  @NotBlank(message = "City is required")
  private String city;

  @NotBlank(message = "Address is required")
  private String address;

  @NotBlank(message = "Tax ID is required")
  private String taxId;

  // Admin User Information
  @NotBlank(message = "Admin first name is required")
  private String adminFirstName;

  @NotBlank(message = "Admin last name is required")
  private String adminLastName;

  @NotBlank(message = "Admin username is required")
  private String adminUsername;

  @NotBlank(message = "Admin email is required")
  @Email(message = "Invalid admin email format")
  private String adminEmail;

  @NotBlank(message = "Admin password is required")
  @Size(min = 6, message = "Password must be at least 6 characters long")
  private String adminPassword;

  @NotBlank(message = "Phone number is required")
  @Pattern(regexp = "^(09|03)\\d{8}$", message = "Phone number must be valid")
  private String adminPhoneNumber;

  private String adminProfilePictureUrl;
}