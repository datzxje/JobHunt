package com.jobhunt.service.impl;

import com.jobhunt.exception.BadRequestException;
import com.jobhunt.exception.ResourceNotFoundException;
import com.jobhunt.mapper.CompanyMapper;
import com.jobhunt.model.entity.Company;
import com.jobhunt.model.request.CompanyRequest;
import com.jobhunt.model.response.CompanyResponse;
import com.jobhunt.repository.CompanyRepository;
import com.jobhunt.repository.UserRepository;
import com.jobhunt.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {

  private final CompanyRepository companyRepository;
  private final UserRepository userRepository;
  private final CompanyMapper companyMapper;

  @Override
  @Transactional
  public CompanyResponse createCompany(CompanyRequest request) {
    String currentUserId = SecurityContextHolder.getContext().getAuthentication().getName();

    var user = userRepository.findById(Long.parseLong(currentUserId))
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    Company company = companyMapper.toEntity(request);
    company.setUser(user);

    return companyMapper.toResponse(companyRepository.save(company));
  }

  @Override
  @Transactional
  public CompanyResponse updateCompany(Long id, CompanyRequest request) {
    String currentUserId = SecurityContextHolder.getContext().getAuthentication().getName();

    Company company = companyRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Company not found"));

    if (!company.getUser().getId().equals(currentUserId)) {
      throw new BadRequestException("You don't have permission to update this company");
    }

    companyMapper.updateCompanyFromDto(request, company);
    return companyMapper.toResponse(companyRepository.save(company));
  }

  @Override
  @Transactional
  public void deleteCompany(Long id) {
    String currentUserId = SecurityContextHolder.getContext().getAuthentication().getName();

    Company company = companyRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Company not found"));

    if (!company.getUser().getId().equals(currentUserId)) {
      throw new BadRequestException("You don't have permission to delete this company");
    }

    company.setActive(false);
    companyRepository.save(company);
  }

  @Override
  public CompanyResponse getCompany(Long id) {
    return companyRepository.findById(id)
        .map(companyMapper::toResponse)
        .orElseThrow(() -> new ResourceNotFoundException("Company not found"));
  }

  @Override
  public CompanyResponse getCurrentUserCompany() {
    String currentUserId = SecurityContextHolder.getContext().getAuthentication().getName();

    return companyRepository.findByUserIdAndActiveTrue(Long.parseLong(currentUserId))
        .map(companyMapper::toResponse)
        .orElseThrow(() -> new ResourceNotFoundException("Company not found for current user"));
  }
}