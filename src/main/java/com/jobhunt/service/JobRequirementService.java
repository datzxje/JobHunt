package com.jobhunt.service;

import com.jobhunt.model.entity.RequirementType;
import com.jobhunt.model.request.JobRequirementRequest;
import com.jobhunt.model.response.JobRequirementResponse;

import java.util.List;

public interface JobRequirementService {

  // CRUD Operations
  JobRequirementResponse createRequirement(Long jobId, JobRequirementRequest request);

  JobRequirementResponse updateRequirement(Long jobId, Long requirementId, JobRequirementRequest request);

  void deleteRequirement(Long jobId, Long requirementId);

  JobRequirementResponse getRequirement(Long jobId, Long requirementId);

  List<JobRequirementResponse> getJobRequirements(Long jobId);

  List<JobRequirementResponse> getJobRequirementsByType(Long jobId, RequirementType type);

  List<JobRequirementResponse> getMandatoryRequirements(Long jobId);

  // Bulk operations
  List<JobRequirementResponse> createMultipleRequirements(Long jobId, List<JobRequirementRequest> requests);

  void deleteAllJobRequirements(Long jobId);

  // Future ranking/filtering methods
  List<JobRequirementResponse> getHighPriorityRequirements(Long jobId, Integer minWeight);

  List<Long> findJobsWithRequirementType(RequirementType type, Integer minWeight);

  // Validation
  boolean hasRequirementType(Long jobId, RequirementType type);
}