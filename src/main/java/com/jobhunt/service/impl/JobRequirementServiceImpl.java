package com.jobhunt.service.impl;

import com.jobhunt.exception.ResourceNotFoundException;
import com.jobhunt.mapper.JobRequirementMapper;
import com.jobhunt.model.entity.Job;
import com.jobhunt.model.entity.JobRequirement;
import com.jobhunt.model.entity.RequirementType;
import com.jobhunt.model.request.JobRequirementRequest;
import com.jobhunt.model.response.JobRequirementResponse;
import com.jobhunt.repository.JobRepository;
import com.jobhunt.repository.JobRequirementRepository;
import com.jobhunt.service.JobRequirementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class JobRequirementServiceImpl implements JobRequirementService {

  private final JobRequirementRepository jobRequirementRepository;
  private final JobRepository jobRepository;
  private final JobRequirementMapper jobRequirementMapper;

  @Override
  public JobRequirementResponse createRequirement(Long jobId, JobRequirementRequest request) {
    log.info("Creating requirement for job: {}, type: {}", jobId, request.getType());

    // Validate job exists
    Job job = findJobById(jobId);

    // Create and save requirement
    JobRequirement requirement = jobRequirementMapper.toEntity(request);
    requirement.setJob(job);

    JobRequirement savedRequirement = jobRequirementRepository.save(requirement);

    log.info("Created requirement with ID: {} for job: {}", savedRequirement.getId(), jobId);
    return jobRequirementMapper.toResponse(savedRequirement);
  }

  @Override
  public JobRequirementResponse updateRequirement(Long jobId, Long requirementId, JobRequirementRequest request) {
    log.info("Updating requirement: {} for job: {}", requirementId, jobId);

    // Validate job and requirement exist
    validateJobExists(jobId);
    JobRequirement requirement = findRequirementById(requirementId);

    // Validate requirement belongs to the job
    if (!requirement.getJob().getId().equals(jobId)) {
      throw new IllegalArgumentException("Requirement does not belong to the specified job");
    }

    // Update requirement
    jobRequirementMapper.updateEntity(requirement, request);
    JobRequirement updatedRequirement = jobRequirementRepository.save(requirement);

    log.info("Updated requirement: {} for job: {}", requirementId, jobId);
    return jobRequirementMapper.toResponse(updatedRequirement);
  }

  @Override
  public void deleteRequirement(Long jobId, Long requirementId) {
    log.info("Deleting requirement: {} for job: {}", requirementId, jobId);

    // Validate job exists
    validateJobExists(jobId);

    // Find and validate requirement
    JobRequirement requirement = findRequirementById(requirementId);

    // Validate requirement belongs to the job
    if (!requirement.getJob().getId().equals(jobId)) {
      throw new IllegalArgumentException("Requirement does not belong to the specified job");
    }

    jobRequirementRepository.delete(requirement);
    log.info("Deleted requirement: {} for job: {}", requirementId, jobId);
  }

  @Override
  @Transactional(readOnly = true)
  public JobRequirementResponse getRequirement(Long jobId, Long requirementId) {
    log.debug("Getting requirement: {} for job: {}", requirementId, jobId);

    // Validate job exists
    validateJobExists(jobId);

    // Find requirement
    JobRequirement requirement = findRequirementById(requirementId);

    // Validate requirement belongs to the job
    if (!requirement.getJob().getId().equals(jobId)) {
      throw new IllegalArgumentException("Requirement does not belong to the specified job");
    }

    return jobRequirementMapper.toResponse(requirement);
  }

  @Override
  @Transactional(readOnly = true)
  public List<JobRequirementResponse> getJobRequirements(Long jobId) {
    log.debug("Getting all requirements for job: {}", jobId);

    // Validate job exists
    validateJobExists(jobId);

    List<JobRequirement> requirements = jobRequirementRepository.findByJobIdOrderByWeightDesc(jobId);
    return jobRequirementMapper.toResponseList(requirements);
  }

  @Override
  @Transactional(readOnly = true)
  public List<JobRequirementResponse> getJobRequirementsByType(Long jobId, RequirementType type) {
    log.debug("Getting requirements for job: {}, type: {}", jobId, type);

    // Validate job exists
    validateJobExists(jobId);

    List<JobRequirement> requirements = jobRequirementRepository.findByJobIdAndType(jobId, type);
    return jobRequirementMapper.toResponseList(requirements);
  }

  @Override
  @Transactional(readOnly = true)
  public List<JobRequirementResponse> getMandatoryRequirements(Long jobId) {
    log.debug("Getting mandatory requirements for job: {}", jobId);

    // Validate job exists
    validateJobExists(jobId);

    List<JobRequirement> requirements = jobRequirementRepository.findByJobIdAndIsMandatoryTrue(jobId);
    return jobRequirementMapper.toResponseList(requirements);
  }

  @Override
  public List<JobRequirementResponse> createMultipleRequirements(Long jobId, List<JobRequirementRequest> requests) {
    log.info("Creating {} requirements for job: {}", requests.size(), jobId);

    // Validate job exists
    Job job = findJobById(jobId);

    List<JobRequirement> requirements = requests.stream()
        .map(request -> {
          JobRequirement requirement = jobRequirementMapper.toEntity(request);
          requirement.setJob(job);
          return requirement;
        })
        .toList();

    List<JobRequirement> savedRequirements = jobRequirementRepository.saveAll(requirements);

    log.info("Created {} requirements for job: {}", savedRequirements.size(), jobId);
    return jobRequirementMapper.toResponseList(savedRequirements);
  }

  @Override
  public void deleteAllJobRequirements(Long jobId) {
    log.info("Deleting all requirements for job: {}", jobId);

    // Validate job exists
    validateJobExists(jobId);

    jobRequirementRepository.deleteByJobId(jobId);
    log.info("Deleted all requirements for job: {}", jobId);
  }

  @Override
  @Transactional(readOnly = true)
  public List<JobRequirementResponse> getHighPriorityRequirements(Long jobId, Integer minWeight) {
    log.debug("Getting high priority requirements for job: {}, minWeight: {}", jobId, minWeight);

    // Validate job exists
    validateJobExists(jobId);

    List<JobRequirement> requirements = jobRequirementRepository.findByJobIdAndMinWeight(jobId, minWeight);
    return jobRequirementMapper.toResponseList(requirements);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Long> findJobsWithRequirementType(RequirementType type, Integer minWeight) {
    log.debug("Finding jobs with requirement type: {}, minWeight: {}", type, minWeight);

    return jobRequirementRepository.findJobIdsByTypeAndMinWeight(type, minWeight);
  }

  @Override
  @Transactional(readOnly = true)
  public boolean hasRequirementType(Long jobId, RequirementType type) {
    log.debug("Checking if job: {} has requirement type: {}", jobId, type);

    return jobRequirementRepository.existsByJobIdAndType(jobId, type);
  }

  // Helper methods
  private Job findJobById(Long jobId) {
    return jobRepository.findById(jobId)
        .orElseThrow(() -> new ResourceNotFoundException("Job not found with ID: " + jobId));
  }

  private void validateJobExists(Long jobId) {
    if (!jobRepository.existsById(jobId)) {
      throw new ResourceNotFoundException("Job not found with ID: " + jobId);
    }
  }

  private JobRequirement findRequirementById(Long requirementId) {
    return jobRequirementRepository.findById(requirementId)
        .orElseThrow(() -> new ResourceNotFoundException("Job requirement not found with ID: " + requirementId));
  }
}