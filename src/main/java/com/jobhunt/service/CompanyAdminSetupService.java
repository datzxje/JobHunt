package com.jobhunt.service;

import com.jobhunt.model.request.CompanyAdminSetupRequest;
import com.jobhunt.model.response.CompanyResponse;

public interface CompanyAdminSetupService {

  /**
   * Setup a new company with its admin user in a single transaction
   * This ensures company and admin are created together atomically
   * 
   * @param request Contains both company and admin user information
   * @return CompanyResponse with the created company details
   */
  CompanyResponse setupCompanyWithAdmin(CompanyAdminSetupRequest request);
}