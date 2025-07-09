package com.jobhunt.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoResponse {

  private Long id;
  private Long userId;

  // Main Profile Information
  private String profilePictureUrl;
  private String fullName;
  private String jobTitle;
  private String phone;
  private String email;
  private String website;
  private String currentSalary;
  private String expectedSalary;
  private String experience;
  private String ageRange;
  private String educationLevel;
  private String languages;
  private List<String> categories;
  private Boolean allowSearchListing;
  private String description;

  // Social Network Information
  private String facebookUrl;
  private String twitterUrl;
  private String linkedinUrl;
  private String googlePlusUrl;

  // Contact Information
  private String country;
  private String city;
  private String completeAddress;
  private String mapLocation;
  private BigDecimal latitude;
  private BigDecimal longitude;

  // Profile completion tracking
  private Boolean isProfileComplete;
  private Integer completionPercentage;

  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}