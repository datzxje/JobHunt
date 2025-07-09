package com.jobhunt.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoRequest {

  // Main Profile Information
  @Size(max = 100, message = "Full name cannot exceed 100 characters")
  private String fullName;

  @Size(max = 100, message = "Job title cannot exceed 100 characters")
  private String jobTitle;

  @Size(max = 20, message = "Phone cannot exceed 20 characters")
  private String phone;

  @Email(message = "Please provide a valid email address")
  @Size(max = 100, message = "Email cannot exceed 100 characters")
  private String email;

  @Size(max = 200, message = "Website URL cannot exceed 200 characters")
  private String website;

  private String currentSalary;

  private String expectedSalary;

  @Size(max = 50, message = "Experience cannot exceed 50 characters")
  private String experience;

  private String ageRange;

  @Size(max = 100, message = "Education level cannot exceed 100 characters")
  private String educationLevel;

  @Size(max = 200, message = "Languages cannot exceed 200 characters")
  private String languages;

  private List<String> categories;

  private Boolean allowSearchListing = true;

  @Size(max = 2000, message = "Description cannot exceed 2000 characters")
  private String description;

  // Social Network Information
  @Size(max = 200, message = "Facebook URL cannot exceed 200 characters")
  private String facebookUrl;

  @Size(max = 200, message = "Twitter URL cannot exceed 200 characters")
  private String twitterUrl;

  @Size(max = 200, message = "LinkedIn URL cannot exceed 200 characters")
  private String linkedinUrl;

  @Size(max = 200, message = "Google Plus URL cannot exceed 200 characters")
  private String googlePlusUrl;

  // Contact Information
  @Size(max = 100, message = "Country cannot exceed 100 characters")
  private String country;

  @Size(max = 100, message = "City cannot exceed 100 characters")
  private String city;

  @Size(max = 500, message = "Complete address cannot exceed 500 characters")
  private String completeAddress;

  @Size(max = 200, message = "Map location cannot exceed 200 characters")
  private String mapLocation;

  private BigDecimal latitude;

  private BigDecimal longitude;
}