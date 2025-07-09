package com.jobhunt.model.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class BulkCompanyAdminSetupRequest {

  @NotEmpty(message = "Company setup list cannot be empty")
  @Size(max = 50, message = "Cannot process more than 50 companies at once")
  @Valid
  private List<CompanyAdminSetupRequest> companies;

  private boolean stopOnFirstError = true; // If true, stops processing on first error
}