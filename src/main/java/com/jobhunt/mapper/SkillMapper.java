package com.jobhunt.mapper;

import com.jobhunt.model.entity.Skill;
import com.jobhunt.model.response.SkillResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SkillMapper {
  SkillResponse toResponse(Skill skill);
}