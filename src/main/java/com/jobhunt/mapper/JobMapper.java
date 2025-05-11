package com.jobhunt.mapper;

import com.jobhunt.model.entity.Job;
import com.jobhunt.model.request.JobRequest;
import com.jobhunt.model.response.JobResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, uses = {
    CompanyMapper.class })
public interface JobMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "company", ignore = true)
  @Mapping(target = "applications", ignore = true)
  @Mapping(target = "savedByUsers", ignore = true)
  @Mapping(target = "active", constant = "true")
  Job toEntity(JobRequest request);

  @Mapping(target = "numberOfApplications", expression = "java(job.getApplications() != null ? job.getApplications().size() : 0L)")
  @Mapping(target = "company", source = "company")
  JobResponse toResponse(Job job);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "company", ignore = true)
  @Mapping(target = "applications", ignore = true)
  @Mapping(target = "savedByUsers", ignore = true)
  @Mapping(target = "active", ignore = true)
  void updateJobFromDto(JobRequest request, @MappingTarget Job job);
}