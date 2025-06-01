package com.jobhunt.service;

import com.jobhunt.model.entity.CompanyMember;
import com.jobhunt.model.request.CompanyMemberUpdateRequest;
import com.jobhunt.model.response.CompanyMemberResponse;
import com.jobhunt.model.response.TeamStatsResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CompanyMemberService {

  CompanyMemberResponse addMember(Long userId, Long companyId, CompanyMember.MemberRole role);

  Page<CompanyMemberResponse> getMembersByCompany(Long companyId, Pageable pageable);

  Page<CompanyMemberResponse> getMembersByCompanyAndRole(
      Long companyId,
      CompanyMember.MemberRole role,
      Pageable pageable);

  Page<CompanyMemberResponse> getMembersByCompanyAndStatus(
      Long companyId,
      CompanyMember.MemberStatus status,
      Pageable pageable);

  CompanyMemberResponse getMemberById(Long id);

  CompanyMemberResponse updateMember(Long id, CompanyMemberUpdateRequest request);

  CompanyMemberResponse transferAdminRights(Long currentAdminId, Long newAdminId, Long companyId);

  CompanyMemberResponse toggleMemberStatus(Long id);

  void removeMember(Long id);

  List<CompanyMemberResponse> getMembersByCompanyWithUser(Long companyId);

  TeamStatsResponse getTeamStats(Long companyId);

  boolean isUserMemberOfCompany(Long userId, Long companyId);

  CompanyMemberResponse getUserMembershipInCompany(Long userId, Long companyId);

  boolean isUserAdminOfCompany(Long userId, Long companyId);

  long countMembersByRole(Long companyId, CompanyMember.MemberRole role);

  /**
   * Get companies where the user is an active admin
   * 
   * @param userId The user ID
   * @return List of company memberships where user is admin
   */
  List<CompanyMemberResponse> getAdminMemberships(Long userId);
}