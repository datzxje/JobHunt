package com.jobhunt.service;

import com.jobhunt.model.entity.CompanyJoinRequest;
import com.jobhunt.model.request.CompanyJoinRequestRequest;
import com.jobhunt.model.response.CompanyJoinRequestResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CompanyJoinRequestService {

  CompanyJoinRequestResponse createJoinRequest(CompanyJoinRequestRequest request, Long userId);

  Page<CompanyJoinRequestResponse> getJoinRequestsByCompany(Long companyId, Pageable pageable);

  Page<CompanyJoinRequestResponse> getJoinRequestsByCompanyAndStatus(
      Long companyId,
      CompanyJoinRequest.RequestStatus status,
      Pageable pageable);

  CompanyJoinRequestResponse getJoinRequestById(Long id);

  CompanyJoinRequestResponse approveJoinRequest(Long id, Long reviewerId);

  CompanyJoinRequestResponse rejectJoinRequest(Long id, Long reviewerId);

  List<CompanyJoinRequestResponse> getJoinRequestsByCompanyWithUser(Long companyId);

  long countPendingRequestsByCompany(Long companyId);

  boolean hasExistingRequest(Long userId, Long companyId);

  void deleteJoinRequest(Long id);
}