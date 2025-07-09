package com.jobhunt.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileSectionRequest {

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
  private Boolean allowSearchListing;

  @Size(max = 2000, message = "Description cannot exceed 2000 characters")
  private String description;
}