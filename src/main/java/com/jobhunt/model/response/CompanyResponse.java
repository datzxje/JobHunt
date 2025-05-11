package com.jobhunt.model.response;

import lombok.Data;

@Data
public class CompanyResponse {
  // Company Profile Section
  private String id;
  private String logoUrl;
  private String coverUrl;
  private String name;
  private String email;
  private String phoneNumber;
  private String websiteUrl;
  private Integer establishmentYear;
  private Integer teamSize;
  private String industryType;
  private String about;

  // Social Network Section
  private String facebookUrl;
  private String twitterUrl;
  private String linkedinUrl;
  private String googlePlusUrl;

  // Contact Information Section
  private String country;
  private String city;
  private String address;
  private Double latitude;
  private Double longitude;
  private String taxId;

  // Additional Information
  private Double averageRating;
  private Long totalReviews;
  private Boolean active;
  private String createdAt;
  private String updatedAt;
}