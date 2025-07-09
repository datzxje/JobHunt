package com.jobhunt.service.impl;

import com.jobhunt.exception.BadRequestException;
import com.jobhunt.exception.ResourceNotFoundException;
import com.jobhunt.model.entity.CompanyJoinRequest;
import com.jobhunt.model.entity.CompanyMember;
import com.jobhunt.model.entity.User;
import com.jobhunt.repository.CompanyJoinRequestRepository;
import com.jobhunt.repository.CompanyMemberRepository;
import com.jobhunt.repository.UserRepository;
import com.jobhunt.service.CompanyAuthorizationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompanyAuthorizationServiceImpl implements CompanyAuthorizationService {

  private final CompanyMemberRepository companyMemberRepository;
  private final CompanyJoinRequestRepository joinRequestRepository;
  private final UserRepository userRepository;

  @Override
  @Transactional(readOnly = true)
  public boolean isCurrentUserAdminOfCompany(Long companyId) {
    Long currentUserId = getCurrentUserId();
    if (currentUserId == null) {
      return false;
    }
    return isUserAdminOfCompany(currentUserId, companyId);
  }

  @Override
  @Transactional(readOnly = true)
  public boolean isUserAdminOfCompany(Long userId, Long companyId) {
    return companyMemberRepository.findByUserIdAndCompanyId(userId, companyId)
        .map(member -> member.getRole() == CompanyMember.MemberRole.ADMIN &&
            member.getStatus() == CompanyMember.MemberStatus.ACTIVE)
        .orElse(false);
  }

  @Override
  public Long getCurrentUserId() {
    try {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
        String keycloakId = jwt.getSubject();
        log.debug("Getting user ID for Keycloak ID: {}", keycloakId);

        return userRepository.findByKeycloakId(keycloakId)
            .map(User::getId)
            .orElse(null);
      }
      return null;
    } catch (Exception e) {
      log.error("Error getting current user ID: {}", e.getMessage());
      return null;
    }
  }

  @Override
  @Transactional(readOnly = true)
  public boolean canAccessJoinRequest(Long joinRequestId) {
    return joinRequestRepository.findById(joinRequestId)
        .map(request -> isCurrentUserAdminOfCompany(request.getCompany().getId()))
        .orElse(false);
  }

  @Override
  @Transactional(readOnly = true)
  public boolean canAccessTeamMember(Long memberId) {
    return companyMemberRepository.findById(memberId)
        .map(member -> isCurrentUserAdminOfCompany(member.getCompany().getId()))
        .orElse(false);
  }

  @Override
  @Transactional(readOnly = true)
  public void validateAdminAccess(Long companyId) {
    Long currentUserId = getCurrentUserId();
    if (currentUserId == null) {
      log.warn("No authenticated user found when validating admin access");
      throw new AccessDeniedException("Authentication required");
    }

    if (!isUserAdminOfCompany(currentUserId, companyId)) {
      log.warn("User {} is not an admin of company {}", currentUserId, companyId);
      throw new AccessDeniedException("You are not authorized to access this company's data");
    }

    log.debug("Admin access validated for user {} on company {}", currentUserId, companyId);
  }
}