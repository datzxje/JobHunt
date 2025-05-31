package com.jobhunt.service;

import com.jobhunt.model.request.CompanyRequest;
import com.jobhunt.model.response.CompanyResponse;
import org.springframework.data.domain.Page;

public interface CompanyService {
  CompanyResponse createCompany(CompanyRequest request);

  CompanyResponse updateCompany(Long id, CompanyRequest request);

  void deleteCompany(Long id);

  CompanyResponse getCompany(Long id);

  Page<CompanyResponse> getAllCompanies(int page, int size);

  CompanyResponse getCurrentUserCompany();
}