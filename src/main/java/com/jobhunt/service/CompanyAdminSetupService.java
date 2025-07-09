package com.jobhunt.service;

import com.jobhunt.model.request.CompanyAdminSetupRequest;
import com.jobhunt.model.request.BulkCompanyAdminSetupRequest;
import com.jobhunt.model.response.CompanyResponse;
import com.jobhunt.model.response.BulkCompanyAdminSetupResponse;

public interface CompanyAdminSetupService {

  /**
   * Setup a new company with its admin user in a single transaction
   * This ensures company and admin are created together atomically
   * 
   * @param request Contains both company and admin user information
   * @return CompanyResponse with the created company details
   */
  CompanyResponse setupCompanyWithAdmin(CompanyAdminSetupRequest request);

  /**
   * Setup multiple companies with their admin users in bulk
   * Each company setup is processed individually with error handling
   * 
   * @param request Contains list of company admin setup requests and processing
   *                options
   * @return BulkCompanyAdminSetupResponse with detailed results for each company
   */
  BulkCompanyAdminSetupResponse setupMultipleCompaniesWithAdmins(BulkCompanyAdminSetupRequest request);
}