package com.jobhunt.model.response;

import lombok.Data;

@Data
public class CompanyResponse {
  private String id;
  private String name;
  private String description;
  private String websiteUrl;
  private String address;
  private Integer companySize;
  private Integer establishmentYear;
  private String industryType;
  private Double averageRating;
  private Long totalReviews;
}