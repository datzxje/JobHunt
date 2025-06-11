package com.jobhunt.mapper;

import com.jobhunt.model.entity.JobRequirement;
import com.jobhunt.model.request.JobRequirementRequest;
import com.jobhunt.model.response.JobRequirementResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface JobRequirementMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "job", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  JobRequirement toEntity(JobRequirementRequest request);

  @Mapping(source = "job.id", target = "jobId")
  JobRequirementResponse toResponse(JobRequirement entity);

  List<JobRequirementResponse> toResponseList(List<JobRequirement> entities);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "job", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  void updateEntity(@MappingTarget JobRequirement entity, JobRequirementRequest request);
}