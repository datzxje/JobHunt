package com.jobhunt.mapper;

import com.jobhunt.model.entity.JobCategory;
import com.jobhunt.model.response.JobCategoryResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface JobCategoryMapper {
  JobCategoryResponse toResponse(JobCategory jobCategory);
}