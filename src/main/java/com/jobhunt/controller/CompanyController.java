package com.jobhunt.controller;

import com.jobhunt.model.request.CompanyRequest;
import com.jobhunt.payload.Response;
import com.jobhunt.service.CompanyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/companies")
@RequiredArgsConstructor
public class CompanyController {

  private final CompanyService companyService;

  @PostMapping
  @PreAuthorize("hasRole('EMPLOYER')")
  public ResponseEntity<?> createCompany(@Valid @RequestBody CompanyRequest request) {
    return ResponseEntity.ok(Response.ofSucceeded(companyService.createCompany(request)));
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('EMPLOYER')")
  public ResponseEntity<?> updateCompany(@PathVariable Long id,
      @Valid @RequestBody CompanyRequest request) {
    return ResponseEntity.ok(Response.ofSucceeded(companyService.updateCompany(id, request)));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('EMPLOYER')")
  public ResponseEntity<?> deleteCompany(@PathVariable Long id) {
    companyService.deleteCompany(id);
    return ResponseEntity.ok(Response.ofSucceeded());
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getCompany(@PathVariable Long id) {
    return ResponseEntity.ok(Response.ofSucceeded(companyService.getCompany(id)));
  }

  @GetMapping("/me")
  @PreAuthorize("hasRole('EMPLOYER')")
  public ResponseEntity<?> getCurrentUserCompany() {
    return ResponseEntity.ok(Response.ofSucceeded(companyService.getCurrentUserCompany()));
  }
}