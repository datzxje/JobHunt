package com.jobhunt.mapper;

import com.jobhunt.model.entity.Language;
import com.jobhunt.model.response.LanguageResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LanguageMapper {
  LanguageResponse toResponse(Language language);
}