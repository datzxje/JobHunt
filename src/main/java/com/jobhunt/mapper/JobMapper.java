package com.jobhunt.mapper;

import com.jobhunt.model.entity.Job;
import com.jobhunt.model.entity.JobCategory;
import com.jobhunt.model.entity.Language;
import com.jobhunt.model.entity.Skill;
import com.jobhunt.model.request.JobRequest;
import com.jobhunt.model.response.JobResponse;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.Set;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, uses = {
    CompanyMapper.class, SkillMapper.class, JobCategoryMapper.class, LanguageMapper.class, JobRequirementMapper.class })
public interface JobMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "company", ignore = true)
  @Mapping(target = "applications", ignore = true)
  @Mapping(target = "savedByUsers", ignore = true)
  @Mapping(target = "categories", ignore = true)
  @Mapping(target = "requiredSkills", ignore = true)
  @Mapping(target = "requiredLanguages", ignore = true)
  @Mapping(target = "jobRequirements", ignore = true)
  @Mapping(target = "active", constant = "true")
  Job toEntity(JobRequest request);

  @Mapping(target = "numberOfApplications", expression = "java(job.getApplications() != null ? job.getApplications().size() : 0L)")
  @Mapping(target = "company", source = "company")
  @Mapping(target = "categories", source = "categories")
  @Mapping(target = "requiredSkills", source = "requiredSkills")
  @Mapping(target = "requiredLanguages", source = "requiredLanguages")
  @Mapping(target = "jobRequirements", source = "jobRequirements")
  JobResponse toResponse(Job job);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "company", ignore = true)
  @Mapping(target = "applications", ignore = true)
  @Mapping(target = "savedByUsers", ignore = true)
  @Mapping(target = "categories", ignore = true)
  @Mapping(target = "requiredSkills", ignore = true)
  @Mapping(target = "requiredLanguages", ignore = true)
  @Mapping(target = "jobRequirements", ignore = true)
  @Mapping(target = "active", ignore = true)
  void updateJobFromDto(JobRequest request, @MappingTarget Job job);

  // Helper methods for mapping entity collections by IDs
  default Set<JobCategory> mapCategoryIds(Set<Long> categoryIds, @Context JobMapperContext context) {
    if (categoryIds == null || context == null) {
      return null;
    }
    return context.getCategoriesByIds(categoryIds);
  }

  default Set<Skill> mapSkillIds(Set<Long> skillIds, @Context JobMapperContext context) {
    if (skillIds == null || context == null) {
      return null;
    }
    return context.getSkillsByIds(skillIds);
  }

  default Set<Language> mapLanguageIds(Set<Long> languageIds, @Context JobMapperContext context) {
    if (languageIds == null || context == null) {
      return null;
    }
    return context.getLanguagesByIds(languageIds);
  }
}