package com.jobhunt.mapper;

import com.jobhunt.model.entity.User;
import com.jobhunt.model.request.SignUpRequest;
import com.jobhunt.model.request.UserRequest;
import com.jobhunt.model.response.SignUpResponse;
import com.jobhunt.model.response.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "applications", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "keycloakId", ignore = true)
    @Mapping(target = "refreshToken", ignore = true)
    @Mapping(target = "reviewsGiven", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User toEntity(SignUpRequest signUpRequest);

    @Mapping(target = "firstname", source = "firstName")
    @Mapping(target = "lastname", source = "lastName")
    @Mapping(target = "status", constant = "ACTIVE")
    UserResponse toResponse(User user);

    @Mapping(target = "active", ignore = true)
    @Mapping(target = "applications", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "firstName", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "keycloakId", ignore = true)
    @Mapping(target = "lastName", ignore = true)
    @Mapping(target = "phoneNumber", ignore = true)
    @Mapping(target = "profilePictureUrl", ignore = true)
    @Mapping(target = "refreshToken", ignore = true)
    @Mapping(target = "reviewsGiven", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateUserFromDto(UserRequest userRequest, @MappingTarget User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "keycloakUsername", source = "email")
    @Mapping(target = "status", constant = "PENDING")
    SignUpResponse toResponse(SignUpRequest signUpRequest);

}
