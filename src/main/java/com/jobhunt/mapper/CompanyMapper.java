package com.jobhunt.mapper;

import com.jobhunt.model.entity.Company;
import com.jobhunt.model.entity.Review;
import com.jobhunt.model.request.CompanyRequest;
import com.jobhunt.model.response.CompanyResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CompanyMapper {
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "active", constant = "true")
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "user", ignore = true)
  @Mapping(target = "jobs", ignore = true)
  @Mapping(target = "reviews", ignore = true)
  Company toEntity(CompanyRequest request);

  @Mapping(target = "averageRating", expression = "java(calculateAverageRating(company))")
  @Mapping(target = "totalReviews", expression = "java(company.getReviews() != null ? (long)company.getReviews().size() : 0L)")
  CompanyResponse toResponse(Company company);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "active", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "user", ignore = true)
  @Mapping(target = "jobs", ignore = true)
  @Mapping(target = "reviews", ignore = true)
  void updateCompanyFromDto(CompanyRequest request, @MappingTarget Company company);

  default Double calculateAverageRating(Company company) {
    if (company.getReviews() == null || company.getReviews().isEmpty()) {
      return 0.0;
    }
    return company.getReviews().stream()
        .mapToInt(Review::getRating)
        .average()
        .orElse(0.0);
  }
}