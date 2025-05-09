package com.jobhunt.service;

import com.jobhunt.model.request.CompanyRequest;
import com.jobhunt.model.response.CompanyResponse;

public interface CompanyService {
  CompanyResponse createCompany(CompanyRequest request);

  CompanyResponse updateCompany(Long id, CompanyRequest request);

  void deleteCompany(Long id);

  CompanyResponse getCompany(Long id);

  CompanyResponse getCurrentUserCompany();
}