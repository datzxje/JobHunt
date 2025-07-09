package com.jobhunt.model.response;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BulkCompanyAdminSetupResponse {

  private int totalProcessed;
  private int successCount;
  private int failureCount;
  private List<CompanySetupResult> results = new ArrayList<>();

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class CompanySetupResult {
    private int index; // Index in the original request list
    private boolean success;
    private CompanyResponse company; // Only populated on success
    private String errorMessage; // Only populated on failure
    private String companyName; // For identification when error occurs
    private String adminEmail; // For identification when error occurs
  }

  public void addSuccess(int index, CompanyResponse company, String companyName, String adminEmail) {
    results.add(new CompanySetupResult(index, true, company, null, companyName, adminEmail));
    successCount++;
    totalProcessed++;
  }

  public void addFailure(int index, String errorMessage, String companyName, String adminEmail) {
    results.add(new CompanySetupResult(index, false, null, errorMessage, companyName, adminEmail));
    failureCount++;
    totalProcessed++;
  }
}