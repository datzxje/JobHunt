package com.jobhunt.controller;

import com.jobhunt.model.request.JobRequest;
import com.jobhunt.payload.Response;
import com.jobhunt.service.JobService;
import com.jobhunt.service.SkillService;
import com.jobhunt.service.JobCategoryService;
import com.jobhunt.service.LanguageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/jobs")
@RequiredArgsConstructor
public class JobController {

  private final JobService jobService;
  private final SkillService skillService;
  private final JobCategoryService jobCategoryService;
  private final LanguageService languageService;

  @PostMapping
  @PreAuthorize("hasRole('EMPLOYER')")
  public ResponseEntity<?> createJob(@Valid @RequestBody JobRequest request) {
    return ResponseEntity.ok(Response.ofSucceeded(jobService.createJob(request)));
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('EMPLOYER')")
  public ResponseEntity<?> updateJob(@PathVariable Long id, @Valid @RequestBody JobRequest request) {
    return ResponseEntity.ok(Response.ofSucceeded(jobService.updateJob(id, request)));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('EMPLOYER')")
  public ResponseEntity<?> deleteJob(@PathVariable Long id) {
    jobService.deleteJob(id);
    return ResponseEntity.ok(Response.ofSucceeded());
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getJob(@PathVariable Long id) {
    return ResponseEntity.ok(Response.ofSucceeded(jobService.getJob(id)));
  }

  @GetMapping("/company/{companyId}")
  public ResponseEntity<?> getCompanyJobs(@PathVariable Long companyId) {
    return ResponseEntity.ok(Response.ofSucceeded(jobService.getCompanyJobs(companyId)));
  }

  @GetMapping
  public ResponseEntity<?> getAllJobs(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(required = false) String keyword,
      @RequestParam(required = false) String location,
      @RequestParam(required = false) String jobType,
      @RequestParam(required = false) String experienceLevel,
      @RequestParam(required = false) String salaryRange) {
    return ResponseEntity.ok(Response.ofSucceeded(
        jobService.getAllJobs(page, size, keyword, location, jobType, experienceLevel, salaryRange)));
  }

  @GetMapping("/search")
  public ResponseEntity<?> searchJobs(
      @RequestParam(required = false) String keyword,
      @RequestParam(required = false) String location,
      @RequestParam(required = false) String employmentType,
      @RequestParam(required = false) String experienceLevel,
      @RequestParam(required = false) Boolean isRemote) {
    return ResponseEntity.ok(Response.ofSucceeded(
        jobService.searchJobs(keyword, location, employmentType, experienceLevel, isRemote)));
  }

  @PostMapping("/{id}/apply")
  @PreAuthorize("hasRole('CANDIDATE')")
  public ResponseEntity<?> applyJob(@PathVariable Long id) {
    return ResponseEntity.ok(Response.ofSucceeded(jobService.applyJob(id)));
  }

  @GetMapping("/applied")
  @PreAuthorize("hasRole('CANDIDATE')")
  public ResponseEntity<?> getAppliedJobs(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    return ResponseEntity.ok(Response.ofSucceeded(jobService.getAppliedJobs(page, size)));
  }

  @PostMapping("/{id}/save")
  @PreAuthorize("hasRole('CANDIDATE')")
  public ResponseEntity<?> saveJob(@PathVariable Long id) {
    return ResponseEntity.ok(Response.ofSucceeded(jobService.saveJob(id)));
  }

  @DeleteMapping("/{id}/save")
  @PreAuthorize("hasRole('CANDIDATE')")
  public ResponseEntity<?> unsaveJob(@PathVariable Long id) {
    jobService.unsaveJob(id);
    return ResponseEntity.ok(Response.ofSucceeded());
  }

  @GetMapping("/saved")
  @PreAuthorize("hasRole('CANDIDATE')")
  public ResponseEntity<?> getSavedJobs(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    return ResponseEntity.ok(Response.ofSucceeded(jobService.getSavedJobs(page, size)));
  }

  // Endpoints for form data
  @GetMapping("/form-data/skills")
  public ResponseEntity<?> getAllSkills() {
    return ResponseEntity.ok(Response.ofSucceeded(skillService.getAllActiveSkills()));
  }

  @GetMapping("/form-data/categories")
  public ResponseEntity<?> getAllCategories() {
    return ResponseEntity.ok(Response.ofSucceeded(jobCategoryService.getAllActiveCategories()));
  }

  @GetMapping("/form-data/languages")
  public ResponseEntity<?> getAllLanguages() {
    return ResponseEntity.ok(Response.ofSucceeded(languageService.getAllActiveLanguages()));
  }
}