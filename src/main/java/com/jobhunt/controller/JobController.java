package com.jobhunt.controller;

import com.jobhunt.model.request.JobRequest;
import com.jobhunt.model.response.JobResponse;
import com.jobhunt.payload.Response;
import com.jobhunt.service.JobService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/jobs")
@RequiredArgsConstructor
public class JobController {

  private final JobService jobService;

  @PostMapping
  @PreAuthorize("hasRole('EMPLOYER')")
  public ResponseEntity<?> createJob(@Valid @RequestBody JobRequest request) {
    return ResponseEntity.ok(Response.ofSucceeded(jobService.createJob(request)));
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('EMPLOYER')")
  public ResponseEntity<?> updateJob(@PathVariable String id, @Valid @RequestBody JobRequest request) {
    return ResponseEntity.ok(Response.ofSucceeded(jobService.updateJob(id, request)));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('EMPLOYER')")
  public ResponseEntity<?> deleteJob(@PathVariable String id) {
    jobService.deleteJob(id);
    return ResponseEntity.ok(Response.ofSucceeded());
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getJob(@PathVariable String id) {
    return ResponseEntity.ok(Response.ofSucceeded(jobService.getJob(id)));
  }

  @GetMapping("/company/{companyId}")
  public ResponseEntity<?> getCompanyJobs(@PathVariable String companyId) {
    return ResponseEntity.ok(Response.ofSucceeded(jobService.getCompanyJobs(companyId)));
  }

  @GetMapping
  public ResponseEntity<?> searchJobs(
      @RequestParam(required = false) String keyword,
      @RequestParam(required = false) String location,
      @RequestParam(required = false) String employmentType,
      @RequestParam(required = false) String experienceLevel,
      @RequestParam(required = false) Boolean isRemote) {
    return ResponseEntity.ok(Response.ofSucceeded(
        jobService.searchJobs(keyword, location, employmentType, experienceLevel, isRemote)));
  }
}