package com.jobhunt.mapper;

import com.jobhunt.model.entity.CompanyMember;
import com.jobhunt.model.response.CompanyMemberResponse;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CompanyMemberMapper {

  @Mapping(source = "user.id", target = "userId")
  @Mapping(source = "user.email", target = "userEmail")
  @Mapping(source = "user.profilePictureUrl", target = "userProfilePicture")
  @Mapping(source = "user.phoneNumber", target = "userPhoneNumber")
  @Mapping(source = "company.id", target = "companyId")
  @Mapping(source = "company.name", target = "companyName")
  @Mapping(target = "userName", ignore = true)
  CompanyMemberResponse toResponse(CompanyMember entity);

  List<CompanyMemberResponse> toResponseList(List<CompanyMember> entities);

  @AfterMapping
  default void afterMapping(@MappingTarget CompanyMemberResponse response, CompanyMember entity) {
    if (entity.getUser() != null) {
      response.setUserName(entity.getUser().getFirstName() +
          (entity.getUser().getLastName() != null ? " " + entity.getUser().getLastName() : ""));
    }
  }
}