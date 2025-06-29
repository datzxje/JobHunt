package com.jobhunt.mapper;

import com.jobhunt.model.entity.Company;
import com.jobhunt.model.entity.Review;
import com.jobhunt.model.response.CompanyBasicResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CompanyBasicMapper {

  @Mapping(target = "averageRating", expression = "java(calculateAverageRating(company))")
  @Mapping(target = "totalReviews", expression = "java(company.getReviews() != null ? (long)company.getReviews().size() : 0L)")
  @Mapping(target = "activeJobsCount", ignore = true) // Will be set manually in service
  CompanyBasicResponse toBasicResponse(Company company);

  // Helper method to calculate average rating
  default Double calculateAverageRating(Company company) {
    if (company.getReviews() == null || company.getReviews().isEmpty()) {
      return 0.0;
    }

    double sum = company.getReviews().stream()
        .mapToDouble(Review::getRating)
        .sum();

    return sum / company.getReviews().size();
  }
}