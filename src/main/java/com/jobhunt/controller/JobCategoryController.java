package com.jobhunt.controller;

import com.jobhunt.payload.Response;
import com.jobhunt.service.JobCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/job-categories")
@RequiredArgsConstructor
public class JobCategoryController {

  private final JobCategoryService jobCategoryService;

  @GetMapping
  public ResponseEntity<?> getAllActiveCategories() {
    return ResponseEntity.ok(Response.ofSucceeded(jobCategoryService.getAllActiveCategories()));
  }

  @GetMapping("/search")
  public ResponseEntity<?> searchCategories(@RequestParam String keyword) {
    return ResponseEntity.ok(Response.ofSucceeded(jobCategoryService.searchCategories(keyword)));
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getCategoryById(@PathVariable Long id) {
    return ResponseEntity.ok(Response.ofSucceeded(jobCategoryService.getCategoryById(id)));
  }
}