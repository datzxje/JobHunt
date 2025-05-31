package com.jobhunt.service.impl;

import com.jobhunt.exception.BadRequestException;
import com.jobhunt.exception.ResourceNotFoundException;
import com.jobhunt.mapper.CompanyMapper;
import com.jobhunt.model.entity.Company;
import com.jobhunt.model.request.CompanyRequest;
import com.jobhunt.model.response.CompanyResponse;
import com.jobhunt.model.response.CompanySelectionResponse;
import com.jobhunt.model.response.UserResponse;
import com.jobhunt.repository.CompanyRepository;
import com.jobhunt.repository.UserRepository;
import com.jobhunt.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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

    var user = userRepository.findByKeycloakId(currentUserId)
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

    var user = userRepository.findByKeycloakId(currentUserId)
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    if (!company.getUser().getId().equals(user.getId())) {
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

    var user = userRepository.findByKeycloakId(currentUserId)
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    if (!company.getUser().getId().equals(user.getId())) {
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

  @Transactional(readOnly = true)
  public Page<CompanyResponse> getAllCompanies(int page, int size) {
    Pageable pageable = PageRequest.of(page, size);
    return companyRepository.findAll(pageable)
        .map(companyMapper::toResponse);
  }

  @Override
  public CompanyResponse getCurrentUserCompany() {
    String currentUserId = SecurityContextHolder.getContext().getAuthentication().getName();

    var user = userRepository.findByKeycloakId(currentUserId)
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    return companyRepository.findByUserIdAndActiveTrue(user.getId())
        .map(companyMapper::toResponse)
        .orElseThrow(() -> new ResourceNotFoundException("Company not found for current user"));
  }

  @Override
  @Transactional(readOnly = true)
  public List<CompanySelectionResponse> getCompaniesForSelection() {
    return companyRepository.findByActiveTrue()
        .stream()
        .map(company -> new CompanySelectionResponse(
            company.getId(),
            company.getName(),
            company.getLogoUrl(),
            company.getIndustryType()))
        .collect(Collectors.toList());
  }
}