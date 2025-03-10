package com.jobhunt.mapper;

import com.jobhunt.model.entity.User;
import com.jobhunt.model.request.SignUpRequest;
import com.jobhunt.model.request.UserRequest;
import com.jobhunt.model.response.SignUpResponse;
import com.jobhunt.model.response.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "password", ignore = true)
    User toEntity(SignUpRequest signUpRequest);

    UserResponse toResponse(User user);

    void updateUserFromDto(UserRequest userRequest, @MappingTarget User user);

    SignUpResponse toResponse(SignUpRequest signUpRequest);

}
