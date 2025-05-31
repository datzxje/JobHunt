package com.jobhunt.service.impl;

import com.jobhunt.exception.ResourceNotFoundException;
import com.jobhunt.exception.BadRequestException;
import com.jobhunt.mapper.CompanyMemberMapper;
import com.jobhunt.model.entity.Company;
import com.jobhunt.model.entity.CompanyMember;
import com.jobhunt.model.entity.User;
import com.jobhunt.model.request.CompanyMemberUpdateRequest;
import com.jobhunt.model.response.CompanyMemberResponse;
import com.jobhunt.model.response.TeamStatsResponse;
import com.jobhunt.repository.CompanyMemberRepository;
import com.jobhunt.repository.CompanyRepository;
import com.jobhunt.repository.UserRepository;
import com.jobhunt.service.CompanyMemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CompanyMemberServiceImpl implements CompanyMemberService {

  private final CompanyMemberRepository memberRepository;
  private final UserRepository userRepository;
  private final CompanyRepository companyRepository;
  private final CompanyMemberMapper mapper;

  @Override
  public CompanyMemberResponse addMember(Long userId, Long companyId, CompanyMember.MemberRole role) {
    log.info("Adding member {} to company {} with role {}", userId, companyId, role);

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

    Company company = companyRepository.findById(companyId)
        .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + companyId));

    if (memberRepository.existsByUserIdAndCompanyId(userId, companyId)) {
      throw new BadRequestException("User is already a member of this company");
    }

    CompanyMember member = new CompanyMember();
    member.setUser(user);
    member.setCompany(company);
    member.setRole(role);
    member.setDepartment(role == CompanyMember.MemberRole.ADMIN ? "Management" : "Human Resources");
    member.setStatus(CompanyMember.MemberStatus.ACTIVE);

    CompanyMember saved = memberRepository.save(member);
    log.info("Member added successfully with id: {}", saved.getId());

    return mapper.toResponse(saved);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<CompanyMemberResponse> getMembersByCompany(Long companyId, Pageable pageable) {
    Page<CompanyMember> members = memberRepository.findByCompanyIdOrderByCreatedAtDesc(companyId, pageable);
    return members.map(mapper::toResponse);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<CompanyMemberResponse> getMembersByCompanyAndRole(
      Long companyId, CompanyMember.MemberRole role, Pageable pageable) {
    Page<CompanyMember> members = memberRepository
        .findByCompanyIdAndRoleOrderByCreatedAtDesc(companyId, role, pageable);
    return members.map(mapper::toResponse);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<CompanyMemberResponse> getMembersByCompanyAndStatus(
      Long companyId, CompanyMember.MemberStatus status, Pageable pageable) {
    Page<CompanyMember> members = memberRepository
        .findByCompanyIdAndStatusOrderByCreatedAtDesc(companyId, status, pageable);
    return members.map(mapper::toResponse);
  }

  @Override
  @Transactional(readOnly = true)
  public CompanyMemberResponse getMemberById(Long id) {
    CompanyMember member = memberRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + id));
    return mapper.toResponse(member);
  }

  @Override
  public CompanyMemberResponse updateMember(Long id, CompanyMemberUpdateRequest request) {
    log.info("Updating member {}", id);

    CompanyMember member = memberRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + id));

    // Validate admin count
    if (member.getRole() == CompanyMember.MemberRole.ADMIN &&
        request.getRole() == CompanyMember.MemberRole.HR) {

      long adminCount = memberRepository.countActiveByCompanyIdAndRole(
          member.getCompany().getId(), CompanyMember.MemberRole.ADMIN);

      if (adminCount <= 1) {
        throw new BadRequestException("Cannot remove the last admin from the company");
      }
    }

    member.setRole(request.getRole());
    member.setStatus(request.getStatus());

    if (request.getDepartment() != null) {
      member.setDepartment(request.getDepartment());
    } else {
      member.setDepartment(request.getRole() == CompanyMember.MemberRole.ADMIN ? "Management" : "Human Resources");
    }

    CompanyMember saved = memberRepository.save(member);
    log.info("Member updated successfully");

    return mapper.toResponse(saved);
  }

  @Override
  public CompanyMemberResponse transferAdminRights(Long currentAdminId, Long newAdminId, Long companyId) {
    log.info("Transferring admin rights from {} to {} for company {}",
        currentAdminId, newAdminId, companyId);

    CompanyMember currentAdmin = memberRepository.findByUserIdAndCompanyId(currentAdminId, companyId)
        .orElseThrow(() -> new ResourceNotFoundException("Current admin not found"));

    CompanyMember newAdmin = memberRepository.findByUserIdAndCompanyId(newAdminId, companyId)
        .orElseThrow(() -> new ResourceNotFoundException("New admin candidate not found"));

    if (currentAdmin.getRole() != CompanyMember.MemberRole.ADMIN) {
      throw new BadRequestException("Current user is not an admin");
    }

    // Transfer rights
    currentAdmin.setRole(CompanyMember.MemberRole.HR);
    currentAdmin.setDepartment("Human Resources");

    newAdmin.setRole(CompanyMember.MemberRole.ADMIN);
    newAdmin.setDepartment("Management");

    // Update company admin
    Company company = companyRepository.findById(companyId)
        .orElseThrow(() -> new ResourceNotFoundException("Company not found"));
    company.setAdminUser(newAdmin.getUser());

    memberRepository.save(currentAdmin);
    CompanyMember saved = memberRepository.save(newAdmin);
    companyRepository.save(company);

    log.info("Admin rights transferred successfully");
    return mapper.toResponse(saved);
  }

  @Override
  public CompanyMemberResponse toggleMemberStatus(Long id) {
    log.info("Toggling status for member {}", id);

    CompanyMember member = memberRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + id));

    // Cannot deactivate the last admin
    if (member.getRole() == CompanyMember.MemberRole.ADMIN &&
        member.getStatus() == CompanyMember.MemberStatus.ACTIVE) {

      long activeAdminCount = memberRepository.countActiveByCompanyIdAndRole(
          member.getCompany().getId(), CompanyMember.MemberRole.ADMIN);

      if (activeAdminCount <= 1) {
        throw new BadRequestException("Cannot deactivate the last admin");
      }
    }

    member.setStatus(member.getStatus() == CompanyMember.MemberStatus.ACTIVE ? CompanyMember.MemberStatus.INACTIVE
        : CompanyMember.MemberStatus.ACTIVE);

    CompanyMember saved = memberRepository.save(member);
    log.info("Member status toggled successfully");

    return mapper.toResponse(saved);
  }

  @Override
  public void removeMember(Long id) {
    log.info("Removing member {}", id);

    CompanyMember member = memberRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + id));

    // Cannot remove the last admin
    if (member.getRole() == CompanyMember.MemberRole.ADMIN) {
      long adminCount = memberRepository.countByCompanyIdAndRole(
          member.getCompany().getId(), CompanyMember.MemberRole.ADMIN);

      if (adminCount <= 1) {
        throw new BadRequestException("Cannot remove the last admin from the company");
      }
    }

    memberRepository.deleteById(id);
    log.info("Member removed successfully");
  }

  @Override
  @Transactional(readOnly = true)
  public List<CompanyMemberResponse> getMembersByCompanyWithUser(Long companyId) {
    List<CompanyMember> members = memberRepository.findByCompanyIdWithUser(companyId);
    return mapper.toResponseList(members);
  }

  @Override
  @Transactional(readOnly = true)
  public TeamStatsResponse getTeamStats(Long companyId) {
    TeamStatsResponse stats = new TeamStatsResponse();

    stats.setTotalMembers(memberRepository.countByCompanyId(companyId));
    stats.setAdmins(memberRepository.countByCompanyIdAndRole(companyId, CompanyMember.MemberRole.ADMIN));
    stats.setHrMembers(memberRepository.countByCompanyIdAndRole(companyId, CompanyMember.MemberRole.HR));
    stats.setActiveMembers(memberRepository.countByCompanyIdAndStatus(companyId, CompanyMember.MemberStatus.ACTIVE));
    stats
        .setInactiveMembers(memberRepository.countByCompanyIdAndStatus(companyId, CompanyMember.MemberStatus.INACTIVE));

    // Get pending requests count (would need to inject
    // CompanyJoinRequestRepository)
    // For now, set to 0 or calculate separately
    stats.setPendingRequests(0);

    return stats;
  }

  @Override
  @Transactional(readOnly = true)
  public boolean isUserMemberOfCompany(Long userId, Long companyId) {
    return memberRepository.existsByUserIdAndCompanyId(userId, companyId);
  }

  @Override
  @Transactional(readOnly = true)
  public CompanyMemberResponse getUserMembershipInCompany(Long userId, Long companyId) {
    CompanyMember member = memberRepository.findByUserIdAndCompanyId(userId, companyId)
        .orElseThrow(() -> new ResourceNotFoundException("User is not a member of this company"));
    return mapper.toResponse(member);
  }

  @Override
  @Transactional(readOnly = true)
  public boolean isUserAdminOfCompany(Long userId, Long companyId) {
    return memberRepository.findByUserIdAndCompanyId(userId, companyId)
        .map(member -> member.getRole() == CompanyMember.MemberRole.ADMIN &&
            member.getStatus() == CompanyMember.MemberStatus.ACTIVE)
        .orElse(false);
  }

  @Override
  @Transactional(readOnly = true)
  public long countMembersByRole(Long companyId, CompanyMember.MemberRole role) {
    return memberRepository.countByCompanyIdAndRole(companyId, role);
  }
}