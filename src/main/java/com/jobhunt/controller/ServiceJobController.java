package com.jobhunt.controller;

import com.jobhunt.model.entity.ServiceJob;
import com.jobhunt.model.request.ServiceJobRequest;
import com.jobhunt.payload.Response;
import com.jobhunt.service.ServiceJobService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/service-jobs")
@RequiredArgsConstructor
public class ServiceJobController {

  private final ServiceJobService serviceJobService;

  @PostMapping
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<?> createServiceJob(
      @Valid @RequestBody ServiceJobRequest request,
      @RequestParam Long userId) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(Response.ofSucceeded(serviceJobService.createServiceJob(request, userId)));
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getServiceJobById(@PathVariable Long id) {
    return ResponseEntity.ok(Response.ofSucceeded(serviceJobService.getServiceJobById(id)));
  }

  @GetMapping
  public ResponseEntity<?> getAllServiceJobs() {
    return ResponseEntity.ok(Response.ofSucceeded(serviceJobService.getAllServiceJobs()));
  }

  @GetMapping("/user/{userId}")
  public ResponseEntity<?> getServiceJobsByUser(@PathVariable Long userId) {
    return ResponseEntity.ok(Response.ofSucceeded(serviceJobService.getServiceJobsByUser(userId)));
  }

  @GetMapping("/assigned/{userId}")
  public ResponseEntity<?> getServiceJobsAssignedToUser(@PathVariable Long userId) {
    return ResponseEntity.ok(Response.ofSucceeded(serviceJobService.getServiceJobsAssignedToUser(userId)));
  }

  @GetMapping("/type/{serviceType}")
  public ResponseEntity<?> getServiceJobsByType(
      @PathVariable ServiceJob.ServiceType serviceType) {
    return ResponseEntity.ok(Response.ofSucceeded(serviceJobService.getServiceJobsByType(serviceType)));
  }

  @GetMapping("/status/{status}")
  public ResponseEntity<?> getServiceJobsByStatus(
      @PathVariable ServiceJob.JobStatus status) {
    return ResponseEntity.ok(Response.ofSucceeded(serviceJobService.getServiceJobsByStatus(status)));
  }

  @GetMapping("/urgent")
  public ResponseEntity<?> getUrgentServiceJobs() {
    return ResponseEntity.ok(Response.ofSucceeded(serviceJobService.getUrgentServiceJobs()));
  }

  @GetMapping("/search")
  public ResponseEntity<?> searchServiceJobs(
      @RequestParam(required = false) String location,
      @RequestParam(required = false) BigDecimal minBudget,
      @RequestParam(required = false) BigDecimal maxBudget,
      @RequestParam(required = false) ServiceJob.ServiceType serviceType) {
    return ResponseEntity.ok(Response.ofSucceeded(
        serviceJobService.searchServiceJobs(location, minBudget, maxBudget, serviceType)));
  }

  @PutMapping("/{id}")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<?> updateServiceJob(
      @PathVariable Long id,
      @Valid @RequestBody ServiceJobRequest request) {
    return ResponseEntity.ok(Response.ofSucceeded(serviceJobService.updateServiceJob(id, request)));
  }

  @PutMapping("/{id}/assign")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<?> assignServiceJob(
      @PathVariable Long id,
      @RequestParam Long userId) {
    return ResponseEntity.ok(Response.ofSucceeded(serviceJobService.assignServiceJob(id, userId)));
  }

  @PutMapping("/{id}/status")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<?> updateServiceJobStatus(
      @PathVariable Long id,
      @RequestParam ServiceJob.JobStatus status) {
    return ResponseEntity.ok(Response.ofSucceeded(serviceJobService.updateServiceJobStatus(id, status)));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<?> deleteServiceJob(@PathVariable Long id) {
    serviceJobService.deleteServiceJob(id);
    return ResponseEntity.ok(Response.ofSucceeded());
  }
}