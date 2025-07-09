package com.jobhunt.mapper;

import com.jobhunt.model.entity.UserInfo;
import com.jobhunt.model.request.UserContactInfoRequest;
import com.jobhunt.model.request.UserInfoRequest;
import com.jobhunt.model.request.UserProfileSectionRequest;
import com.jobhunt.model.request.UserSocialNetworkRequest;
import com.jobhunt.model.response.UserInfoResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserInfoMapper {

  UserInfoMapper INSTANCE = Mappers.getMapper(UserInfoMapper.class);

  /**
   * Convert UserInfo entity to UserInfoResponse DTO
   */
  @Mapping(source = "user.id", target = "userId")
  UserInfoResponse toResponse(UserInfo userInfo);

  /**
   * Convert UserInfoRequest DTO to UserInfo entity
   */
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "user", ignore = true)
  @Mapping(target = "profilePictureUrl", ignore = true)
  @Mapping(target = "isProfileComplete", ignore = true)
  @Mapping(target = "completionPercentage", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  UserInfo toEntity(UserInfoRequest request);

  /**
   * Update UserInfo entity from UserInfoRequest (for full profile update)
   */
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "user", ignore = true)
  @Mapping(target = "profilePictureUrl", ignore = true)
  @Mapping(target = "isProfileComplete", ignore = true)
  @Mapping(target = "completionPercentage", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  void updateEntityFromRequest(UserInfoRequest request, @MappingTarget UserInfo userInfo);

  /**
   * Update UserInfo entity from UserProfileSectionRequest (for main profile
   * section update)
   */
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "user", ignore = true)
  @Mapping(target = "profilePictureUrl", ignore = true)
  @Mapping(target = "facebookUrl", ignore = true)
  @Mapping(target = "twitterUrl", ignore = true)
  @Mapping(target = "linkedinUrl", ignore = true)
  @Mapping(target = "googlePlusUrl", ignore = true)
  @Mapping(target = "country", ignore = true)
  @Mapping(target = "city", ignore = true)
  @Mapping(target = "completeAddress", ignore = true)
  @Mapping(target = "mapLocation", ignore = true)
  @Mapping(target = "latitude", ignore = true)
  @Mapping(target = "longitude", ignore = true)
  @Mapping(target = "isProfileComplete", ignore = true)
  @Mapping(target = "completionPercentage", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  void updateEntityFromProfileSection(UserProfileSectionRequest request, @MappingTarget UserInfo userInfo);

  /**
   * Update UserInfo entity from UserSocialNetworkRequest (for social network
   * section update)
   */
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "user", ignore = true)
  @Mapping(target = "profilePictureUrl", ignore = true)
  @Mapping(target = "fullName", ignore = true)
  @Mapping(target = "jobTitle", ignore = true)
  @Mapping(target = "phone", ignore = true)
  @Mapping(target = "email", ignore = true)
  @Mapping(target = "website", ignore = true)
  @Mapping(target = "currentSalary", ignore = true)
  @Mapping(target = "expectedSalary", ignore = true)
  @Mapping(target = "experience", ignore = true)
  @Mapping(target = "ageRange", ignore = true)
  @Mapping(target = "educationLevel", ignore = true)
  @Mapping(target = "languages", ignore = true)
  @Mapping(target = "categories", ignore = true)
  @Mapping(target = "allowSearchListing", ignore = true)
  @Mapping(target = "description", ignore = true)
  @Mapping(target = "country", ignore = true)
  @Mapping(target = "city", ignore = true)
  @Mapping(target = "completeAddress", ignore = true)
  @Mapping(target = "mapLocation", ignore = true)
  @Mapping(target = "latitude", ignore = true)
  @Mapping(target = "longitude", ignore = true)
  @Mapping(target = "isProfileComplete", ignore = true)
  @Mapping(target = "completionPercentage", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  void updateEntityFromSocialNetwork(UserSocialNetworkRequest request, @MappingTarget UserInfo userInfo);

  /**
   * Update UserInfo entity from UserContactInfoRequest (for contact info section
   * update)
   */
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "user", ignore = true)
  @Mapping(target = "profilePictureUrl", ignore = true)
  @Mapping(target = "fullName", ignore = true)
  @Mapping(target = "jobTitle", ignore = true)
  @Mapping(target = "phone", ignore = true)
  @Mapping(target = "email", ignore = true)
  @Mapping(target = "website", ignore = true)
  @Mapping(target = "currentSalary", ignore = true)
  @Mapping(target = "expectedSalary", ignore = true)
  @Mapping(target = "experience", ignore = true)
  @Mapping(target = "ageRange", ignore = true)
  @Mapping(target = "educationLevel", ignore = true)
  @Mapping(target = "languages", ignore = true)
  @Mapping(target = "categories", ignore = true)
  @Mapping(target = "allowSearchListing", ignore = true)
  @Mapping(target = "description", ignore = true)
  @Mapping(target = "facebookUrl", ignore = true)
  @Mapping(target = "twitterUrl", ignore = true)
  @Mapping(target = "linkedinUrl", ignore = true)
  @Mapping(target = "googlePlusUrl", ignore = true)
  @Mapping(target = "isProfileComplete", ignore = true)
  @Mapping(target = "completionPercentage", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  void updateEntityFromContactInfo(UserContactInfoRequest request, @MappingTarget UserInfo userInfo);
}