package com.jobhunt.service.impl;

import com.jobhunt.exception.ResourceNotFoundException;
import com.jobhunt.exception.BadRequestException;
import com.jobhunt.mapper.CompanyJoinRequestMapper;
import com.jobhunt.model.entity.Company;
import com.jobhunt.model.entity.CompanyJoinRequest;
import com.jobhunt.model.entity.CompanyMember;
import com.jobhunt.model.entity.User;
import com.jobhunt.model.request.CompanyJoinRequestRequest;
import com.jobhunt.model.response.CompanyJoinRequestResponse;
import com.jobhunt.repository.CompanyJoinRequestRepository;
import com.jobhunt.repository.CompanyMemberRepository;
import com.jobhunt.repository.CompanyRepository;
import com.jobhunt.repository.UserRepository;
import com.jobhunt.service.CompanyJoinRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CompanyJoinRequestServiceImpl implements CompanyJoinRequestService {

  private final CompanyJoinRequestRepository joinRequestRepository;
  private final CompanyMemberRepository companyMemberRepository;
  private final UserRepository userRepository;
  private final CompanyRepository companyRepository;
  private final CompanyJoinRequestMapper mapper;

  @Override
  public CompanyJoinRequestResponse createJoinRequest(CompanyJoinRequestRequest request, Long userId) {
    log.info("Creating join request for user {} to company {}", userId, request.getCompanyId());

    // Check if user exists
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

    // Check if company exists
    Company company = companyRepository.findById(request.getCompanyId())
        .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + request.getCompanyId()));

    // Check if user is already a member
    if (companyMemberRepository.existsByUserIdAndCompanyId(userId, request.getCompanyId())) {
      throw new BadRequestException("User is already a member of this company");
    }

    // Check if there's already a pending request
    if (joinRequestRepository.existsByUserIdAndCompanyId(userId, request.getCompanyId())) {
      throw new BadRequestException("A join request already exists for this user and company");
    }

    CompanyJoinRequest joinRequest = new CompanyJoinRequest();
    joinRequest.setUser(user);
    joinRequest.setCompany(company);
    joinRequest.setMessage(request.getMessage());
    joinRequest.setStatus(CompanyJoinRequest.RequestStatus.PENDING);

    CompanyJoinRequest saved = joinRequestRepository.save(joinRequest);
    log.info("Join request created successfully with id: {}", saved.getId());

    return mapper.toResponse(saved);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<CompanyJoinRequestResponse> getJoinRequestsByCompany(Long companyId, Pageable pageable) {
    Page<CompanyJoinRequest> requests = joinRequestRepository.findByCompanyIdOrderByCreatedAtDesc(companyId, pageable);
    return requests.map(mapper::toResponse);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<CompanyJoinRequestResponse> getJoinRequestsByCompanyAndStatus(
      Long companyId, CompanyJoinRequest.RequestStatus status, Pageable pageable) {
    Page<CompanyJoinRequest> requests = joinRequestRepository
        .findByCompanyIdAndStatusOrderByCreatedAtDesc(companyId, status, pageable);
    return requests.map(mapper::toResponse);
  }

  @Override
  @Transactional(readOnly = true)
  public CompanyJoinRequestResponse getJoinRequestById(Long id) {
    CompanyJoinRequest request = joinRequestRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Join request not found with id: " + id));
    return mapper.toResponse(request);
  }

  @Override
  public CompanyJoinRequestResponse approveJoinRequest(Long id, Long reviewerId) {
    log.info("Approving join request {} by reviewer {}", id, reviewerId);

    CompanyJoinRequest joinRequest = joinRequestRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Join request not found with id: " + id));

    if (joinRequest.getStatus() != CompanyJoinRequest.RequestStatus.PENDING) {
      throw new BadRequestException("Only pending requests can be approved");
    }

    User reviewer = userRepository.findById(reviewerId)
        .orElseThrow(() -> new ResourceNotFoundException("Reviewer not found with id: " + reviewerId));

    // Update request status
    joinRequest.setStatus(CompanyJoinRequest.RequestStatus.APPROVED);
    joinRequest.setReviewedBy(reviewer);
    joinRequest.setReviewedAt(Instant.now());

    // Create company member
    CompanyMember member = new CompanyMember();
    member.setUser(joinRequest.getUser());
    member.setCompany(joinRequest.getCompany());
    member.setRole(CompanyMember.MemberRole.HR);
    member.setDepartment("Human Resources");
    member.setStatus(CompanyMember.MemberStatus.ACTIVE);

    companyMemberRepository.save(member);
    CompanyJoinRequest saved = joinRequestRepository.save(joinRequest);

    log.info("Join request approved and member created successfully");
    return mapper.toResponse(saved);
  }

  @Override
  public CompanyJoinRequestResponse rejectJoinRequest(Long id, Long reviewerId) {
    log.info("Rejecting join request {} by reviewer {}", id, reviewerId);

    CompanyJoinRequest joinRequest = joinRequestRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Join request not found with id: " + id));

    if (joinRequest.getStatus() != CompanyJoinRequest.RequestStatus.PENDING) {
      throw new BadRequestException("Only pending requests can be rejected");
    }

    User reviewer = userRepository.findById(reviewerId)
        .orElseThrow(() -> new ResourceNotFoundException("Reviewer not found with id: " + reviewerId));

    joinRequest.setStatus(CompanyJoinRequest.RequestStatus.REJECTED);
    joinRequest.setReviewedBy(reviewer);
    joinRequest.setReviewedAt(Instant.now());

    CompanyJoinRequest saved = joinRequestRepository.save(joinRequest);
    log.info("Join request rejected successfully");

    return mapper.toResponse(saved);
  }

  @Override
  @Transactional(readOnly = true)
  public List<CompanyJoinRequestResponse> getJoinRequestsByCompanyWithUser(Long companyId) {
    List<CompanyJoinRequest> requests = joinRequestRepository.findByCompanyIdWithUser(companyId);
    return mapper.toResponseList(requests);
  }

  @Override
  @Transactional(readOnly = true)
  public long countPendingRequestsByCompany(Long companyId) {
    return joinRequestRepository.countPendingRequestsByCompanyId(companyId);
  }

  @Override
  @Transactional(readOnly = true)
  public boolean hasExistingRequest(Long userId, Long companyId) {
    return joinRequestRepository.existsByUserIdAndCompanyId(userId, companyId);
  }

  @Override
  public void deleteJoinRequest(Long id) {
    if (!joinRequestRepository.existsById(id)) {
      throw new ResourceNotFoundException("Join request not found with id: " + id);
    }
    joinRequestRepository.deleteById(id);
    log.info("Join request deleted successfully with id: {}", id);
  }
}