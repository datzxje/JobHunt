package com.jobhunt.mapper;

import com.jobhunt.model.entity.User;
import com.jobhunt.model.response.UserBasicResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserBasicMapper {

  @Mapping(source = "role", target = "role")
  UserBasicResponse toBasicResponse(User user);
}