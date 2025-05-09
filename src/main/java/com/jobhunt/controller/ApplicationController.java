package com.jobhunt.controller;

import com.jobhunt.model.entity.Application;
import com.jobhunt.model.request.ApplicationRequest;
import com.jobhunt.model.request.ApplicationUpdateRequest;
import com.jobhunt.payload.Response;
import com.jobhunt.service.ApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/applications")
@RequiredArgsConstructor
public class ApplicationController {

  private final ApplicationService applicationService;

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<?> apply(
      @Valid @RequestPart("application") ApplicationRequest request,
      @RequestPart("cv") MultipartFile cv) {
    return ResponseEntity.ok(Response.ofSucceeded(applicationService.apply(request, cv)));
  }

  @GetMapping("/{id}")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<?> getApplication(@PathVariable Long id) {
    return ResponseEntity.ok(Response.ofSucceeded(applicationService.getApplication(id)));
  }

  @GetMapping("/my-applications")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<?> getMyApplications(Pageable pageable) {
    return ResponseEntity.ok(Response.ofSucceeded(applicationService.getUserApplications(pageable)));
  }

  @GetMapping("/job/{jobId}")
  @PreAuthorize("hasRole('EMPLOYER')")
  public ResponseEntity<?> getJobApplications(@PathVariable Long jobId, Pageable pageable) {
    return ResponseEntity.ok(Response.ofSucceeded(applicationService.getJobApplications(jobId, pageable)));
  }

  @PatchMapping("/{id}/status")
  @PreAuthorize("hasRole('EMPLOYER')")
  public ResponseEntity<?> updateStatus(
      @PathVariable Long id,
      @RequestParam Application.ApplicationStatus status) {
    return ResponseEntity.ok(Response.ofSucceeded(applicationService.updateStatus(id, status)));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<?> withdrawApplication(@PathVariable Long id) {
    applicationService.withdrawApplication(id);
    return ResponseEntity.ok(Response.ofSucceeded());
  }

  @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<?> updateApplication(
      @PathVariable Long id,
      @Valid @RequestPart("application") ApplicationUpdateRequest request,
      @RequestPart(value = "cv", required = false) MultipartFile cv) {
    return ResponseEntity.ok(Response.ofSucceeded(applicationService.updateApplication(id, request, cv)));
  }
}