package com.jobhunt.controller;

import com.jobhunt.model.request.CompanyAdminSetupRequest;
import com.jobhunt.payload.Response;
import com.jobhunt.service.CompanyAdminSetupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class CompanyAdminSetupController {

  private final CompanyAdminSetupService companyAdminSetupService;

  @PostMapping("/setup-company")
  public ResponseEntity<?> setupCompanyWithAdmin(@Valid @RequestBody CompanyAdminSetupRequest request) {
    return ResponseEntity.ok(Response.ofSucceeded(
        companyAdminSetupService.setupCompanyWithAdmin(request)));
  }
}