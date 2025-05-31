package com.jobhunt.mapper;

import com.jobhunt.model.entity.CompanyJoinRequest;
import com.jobhunt.model.response.CompanyJoinRequestResponse;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CompanyJoinRequestMapper {

  @Mapping(source = "user.id", target = "userId")
  @Mapping(source = "user.email", target = "userEmail")
  @Mapping(source = "user.profilePictureUrl", target = "userProfilePicture")
  @Mapping(source = "company.id", target = "companyId")
  @Mapping(source = "company.name", target = "companyName")
  @Mapping(source = "reviewedBy.id", target = "reviewedBy")
  @Mapping(target = "userName", ignore = true)
  @Mapping(target = "reviewedByName", ignore = true)
  CompanyJoinRequestResponse toResponse(CompanyJoinRequest entity);

  List<CompanyJoinRequestResponse> toResponseList(List<CompanyJoinRequest> entities);

  @AfterMapping
  default void afterMapping(@MappingTarget CompanyJoinRequestResponse response, CompanyJoinRequest entity) {
    if (entity.getUser() != null) {
      response.setUserName(entity.getUser().getFirstName() +
          (entity.getUser().getLastName() != null ? " " + entity.getUser().getLastName() : ""));
    }
    if (entity.getReviewedBy() != null) {
      response.setReviewedByName(entity.getReviewedBy().getFirstName() +
          (entity.getReviewedBy().getLastName() != null ? " " + entity.getReviewedBy().getLastName() : ""));
    }
  }
}