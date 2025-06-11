package com.jobhunt.controller;

import com.jobhunt.model.entity.RequirementType;
import com.jobhunt.model.request.JobRequirementRequest;
import com.jobhunt.model.response.JobRequirementResponse;
import com.jobhunt.payload.Response;
import com.jobhunt.service.JobRequirementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/jobs/{jobId}/requirements")
@RequiredArgsConstructor
@Slf4j
public class JobRequirementController {

  private final JobRequirementService jobRequirementService;

  // Create a new requirement for a job
  @PostMapping
  public ResponseEntity<?> createRequirement(
      @PathVariable Long jobId,
      @Valid @RequestBody JobRequirementRequest request) {

    log.info("Creating requirement for job: {}, type: {}", jobId, request.getType());

    JobRequirementResponse response = jobRequirementService.createRequirement(jobId, request);

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(Response.ofSucceeded(response));
  }

  // Get all requirements for a job
  @GetMapping
  public ResponseEntity<?> getJobRequirements(
      @PathVariable Long jobId) {

    log.info("Getting all requirements for job: {}", jobId);

    List<JobRequirementResponse> requirements = jobRequirementService.getJobRequirements(jobId);

    return ResponseEntity.ok(Response.ofSucceeded(requirements));
  }

  // Get a specific requirement
  @GetMapping("/{requirementId}")
  public ResponseEntity<?> getRequirement(
      @PathVariable Long jobId,
      @PathVariable Long requirementId) {

    log.info("Getting requirement: {} for job: {}", requirementId, jobId);

    JobRequirementResponse requirement = jobRequirementService.getRequirement(jobId, requirementId);

    return ResponseEntity.ok(Response.ofSucceeded(requirement));
  }

  // Update a requirement
  @PutMapping("/{requirementId}")
  public ResponseEntity<?> updateRequirement(
      @PathVariable Long jobId,
      @PathVariable Long requirementId,
      @Valid @RequestBody JobRequirementRequest request) {

    log.info("Updating requirement: {} for job: {}", requirementId, jobId);

    JobRequirementResponse response = jobRequirementService.updateRequirement(jobId, requirementId, request);

    return ResponseEntity.ok(Response.ofSucceeded(response));
  }

  // Delete a requirement
  @DeleteMapping("/{requirementId}")
  public ResponseEntity<?> deleteRequirement(
      @PathVariable Long jobId,
      @PathVariable Long requirementId) {

    log.info("Deleting requirement: {} for job: {}", requirementId, jobId);

    jobRequirementService.deleteRequirement(jobId, requirementId);

    return ResponseEntity.ok(Response.ofSucceeded());
  }

  // Get requirements by type
  @GetMapping("/type/{type}")
  public ResponseEntity<?> getRequirementsByType(
      @PathVariable Long jobId,
      @PathVariable RequirementType type) {

    log.info("Getting requirements for job: {}, type: {}", jobId, type);

    List<JobRequirementResponse> requirements = jobRequirementService.getJobRequirementsByType(jobId, type);

    return ResponseEntity.ok(Response.ofSucceeded(requirements));
  }

  // Get mandatory requirements
  @GetMapping("/mandatory")
  public ResponseEntity<?> getMandatoryRequirements(
      @PathVariable Long jobId) {

    log.info("Getting mandatory requirements for job: {}", jobId);

    List<JobRequirementResponse> requirements = jobRequirementService.getMandatoryRequirements(jobId);

    return ResponseEntity.ok(Response.ofSucceeded(requirements));
  }

  // Get high priority requirements
  @GetMapping("/priority")
  public ResponseEntity<?> getHighPriorityRequirements(
      @PathVariable Long jobId,
      @RequestParam(defaultValue = "7") Integer minWeight) {

    log.info("Getting high priority requirements for job: {}, minWeight: {}", jobId, minWeight);

    List<JobRequirementResponse> requirements = jobRequirementService.getHighPriorityRequirements(jobId, minWeight);

    return ResponseEntity.ok(Response.ofSucceeded(requirements));
  }

  // Create multiple requirements at once
  @PostMapping("/bulk")
  public ResponseEntity<?> createMultipleRequirements(
      @PathVariable Long jobId,
      @Valid @RequestBody List<JobRequirementRequest> requests) {

    log.info("Creating {} requirements for job: {}", requests.size(), jobId);

    List<JobRequirementResponse> responses = jobRequirementService.createMultipleRequirements(jobId, requests);

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(Response.ofSucceeded(responses));
  }

  // Delete all requirements for a job
  @DeleteMapping("/all")
  public ResponseEntity<?> deleteAllRequirements(
      @PathVariable Long jobId) {

    log.info("Deleting all requirements for job: {}", jobId);

    jobRequirementService.deleteAllJobRequirements(jobId);

    return ResponseEntity.ok(Response.ofSucceeded());
  }
}

// Additional controller for global requirement queries
@RestController
@RequestMapping("/api/v1/requirements")
@RequiredArgsConstructor
@Slf4j
class RequirementQueryController {

  private final JobRequirementService jobRequirementService;

  // Find jobs with specific requirement type (for future ranking system)
  @GetMapping("/jobs")
  public ResponseEntity<?> findJobsWithRequirementType(
      @RequestParam RequirementType type,
      @RequestParam(defaultValue = "5") Integer minWeight) {

    log.info("Finding jobs with requirement type: {}, minWeight: {}", type, minWeight);

    List<Long> jobIds = jobRequirementService.findJobsWithRequirementType(type, minWeight);

    return ResponseEntity.ok(Response.ofSucceeded(jobIds));
  }
}