package com.jobhunt.model.response;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CompanyBasicResponse {
  private Long id;
  private String name;
  private String logoUrl;
  private String industryType;
  private String teamSize;
  private String websiteUrl;
  private String country;
  private String city;
  private String address;
  private Double averageRating;
  private Long totalReviews;
  private Long activeJobsCount;
}