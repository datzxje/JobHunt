package com.jobhunt.mapper;

import com.jobhunt.model.entity.Job;
import com.jobhunt.model.request.JobRequest;
import com.jobhunt.model.response.JobResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface JobMapper {
  JobMapper INSTANCE = Mappers.getMapper(JobMapper.class);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "company", ignore = true)
  @Mapping(target = "applications", ignore = true)
  @Mapping(target = "active", constant = "true")
  Job toEntity(JobRequest request);

  @Mapping(target = "numberOfApplications", expression = "java(job.getApplications().size())")
  JobResponse toResponse(Job job);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "company", ignore = true)
  @Mapping(target = "applications", ignore = true)
  @Mapping(target = "active", ignore = true)
  void updateJobFromDto(JobRequest request, @MappingTarget Job job);
}