package com.jobhunt.controller;

import com.jobhunt.model.entity.CompanyJoinRequest;
import com.jobhunt.model.entity.CompanyMember;
import com.jobhunt.model.request.CompanyJoinRequestRequest;
import com.jobhunt.model.request.CompanyMemberUpdateRequest;
import com.jobhunt.model.response.CompanyJoinRequestResponse;
import com.jobhunt.model.response.CompanyMemberResponse;
import com.jobhunt.model.response.TeamStatsResponse;
import com.jobhunt.service.CompanyJoinRequestService;
import com.jobhunt.service.CompanyMemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/company-admin")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class CompanyAdminController {

  private final CompanyJoinRequestService joinRequestService;
  private final CompanyMemberService memberService;

  // =============== JOIN REQUESTS ENDPOINTS ===============

  @GetMapping("/join-requests")
  @PreAuthorize("hasRole('EMPLOYER') or hasRole('ADMIN')")
  public ResponseEntity<Page<CompanyJoinRequestResponse>> getJoinRequests(
      @RequestParam Long companyId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(required = false) String status) {

    log.info("Getting join requests for company: {}, page: {}, size: {}, status: {}",
        companyId, page, size, status);

    Pageable pageable = PageRequest.of(page, size);
    Page<CompanyJoinRequestResponse> requests;

    if (status != null && !status.isEmpty()) {
      CompanyJoinRequest.RequestStatus requestStatus = CompanyJoinRequest.RequestStatus.valueOf(status.toUpperCase());
      requests = joinRequestService.getJoinRequestsByCompanyAndStatus(companyId, requestStatus, pageable);
    } else {
      requests = joinRequestService.getJoinRequestsByCompany(companyId, pageable);
    }

    return ResponseEntity.ok(requests);
  }

  @GetMapping("/join-requests/{id}")
  @PreAuthorize("hasRole('EMPLOYER') or hasRole('ADMIN')")
  public ResponseEntity<CompanyJoinRequestResponse> getJoinRequestById(@PathVariable Long id) {
    log.info("Getting join request by id: {}", id);
    CompanyJoinRequestResponse request = joinRequestService.getJoinRequestById(id);
    return ResponseEntity.ok(request);
  }

  @PostMapping("/join-requests")
  @PreAuthorize("hasRole('CANDIDATE') or hasRole('EMPLOYER')")
  public ResponseEntity<CompanyJoinRequestResponse> createJoinRequest(
      @Valid @RequestBody CompanyJoinRequestRequest request,
      @RequestParam Long userId) {

    log.info("Creating join request for user: {} to company: {}", userId, request.getCompanyId());
    CompanyJoinRequestResponse response = joinRequestService.createJoinRequest(request, userId);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @PutMapping("/join-requests/{id}/approve")
  @PreAuthorize("hasRole('EMPLOYER') or hasRole('ADMIN')")
  public ResponseEntity<CompanyJoinRequestResponse> approveJoinRequest(
      @PathVariable Long id,
      @RequestParam Long reviewerId) {

    log.info("Approving join request: {} by reviewer: {}", id, reviewerId);
    CompanyJoinRequestResponse response = joinRequestService.approveJoinRequest(id, reviewerId);
    return ResponseEntity.ok(response);
  }

  @PutMapping("/join-requests/{id}/reject")
  @PreAuthorize("hasRole('EMPLOYER') or hasRole('ADMIN')")
  public ResponseEntity<CompanyJoinRequestResponse> rejectJoinRequest(
      @PathVariable Long id,
      @RequestParam Long reviewerId) {

    log.info("Rejecting join request: {} by reviewer: {}", id, reviewerId);
    CompanyJoinRequestResponse response = joinRequestService.rejectJoinRequest(id, reviewerId);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/join-requests/{id}")
  @PreAuthorize("hasRole('EMPLOYER') or hasRole('ADMIN')")
  public ResponseEntity<Void> deleteJoinRequest(@PathVariable Long id) {
    log.info("Deleting join request: {}", id);
    joinRequestService.deleteJoinRequest(id);
    return ResponseEntity.noContent().build();
  }

  // =============== TEAM MEMBERS ENDPOINTS ===============

  @GetMapping("/team-members")
  @PreAuthorize("hasRole('EMPLOYER') or hasRole('ADMIN')")
  public ResponseEntity<Page<CompanyMemberResponse>> getTeamMembers(
      @RequestParam Long companyId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(required = false) String role,
      @RequestParam(required = false) String status) {

    log.info("Getting team members for company: {}, page: {}, size: {}, role: {}, status: {}",
        companyId, page, size, role, status);

    Pageable pageable = PageRequest.of(page, size);
    Page<CompanyMemberResponse> members;

    if (role != null && !role.isEmpty()) {
      CompanyMember.MemberRole memberRole = CompanyMember.MemberRole.valueOf(role.toUpperCase());
      members = memberService.getMembersByCompanyAndRole(companyId, memberRole, pageable);
    } else if (status != null && !status.isEmpty()) {
      CompanyMember.MemberStatus memberStatus = CompanyMember.MemberStatus.valueOf(status.toUpperCase());
      members = memberService.getMembersByCompanyAndStatus(companyId, memberStatus, pageable);
    } else {
      members = memberService.getMembersByCompany(companyId, pageable);
    }

    return ResponseEntity.ok(members);
  }

  @GetMapping("/team-members/{id}")
  @PreAuthorize("hasRole('EMPLOYER') or hasRole('ADMIN')")
  public ResponseEntity<CompanyMemberResponse> getTeamMemberById(@PathVariable Long id) {
    log.info("Getting team member by id: {}", id);
    CompanyMemberResponse member = memberService.getMemberById(id);
    return ResponseEntity.ok(member);
  }

  @PostMapping("/team-members")
  @PreAuthorize("hasRole('EMPLOYER') or hasRole('ADMIN')")
  public ResponseEntity<CompanyMemberResponse> addTeamMember(
      @RequestParam Long userId,
      @RequestParam Long companyId,
      @RequestParam(defaultValue = "HR") String role) {

    log.info("Adding team member: {} to company: {} with role: {}", userId, companyId, role);
    CompanyMember.MemberRole memberRole = CompanyMember.MemberRole.valueOf(role.toUpperCase());
    CompanyMemberResponse response = memberService.addMember(userId, companyId, memberRole);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @PutMapping("/team-members/{id}")
  @PreAuthorize("hasRole('EMPLOYER') or hasRole('ADMIN')")
  public ResponseEntity<CompanyMemberResponse> updateTeamMember(
      @PathVariable Long id,
      @Valid @RequestBody CompanyMemberUpdateRequest request) {

    log.info("Updating team member: {}", id);
    CompanyMemberResponse response = memberService.updateMember(id, request);
    return ResponseEntity.ok(response);
  }

  @PutMapping("/team-members/{id}/transfer-admin")
  @PreAuthorize("hasRole('EMPLOYER') or hasRole('ADMIN')")
  public ResponseEntity<CompanyMemberResponse> transferAdminRights(
      @PathVariable Long id,
      @RequestParam Long currentAdminId,
      @RequestParam Long companyId) {

    log.info("Transferring admin rights from: {} to: {} for company: {}", currentAdminId, id, companyId);
    CompanyMemberResponse response = memberService.transferAdminRights(currentAdminId, id, companyId);
    return ResponseEntity.ok(response);
  }

  @PutMapping("/team-members/{id}/toggle-status")
  @PreAuthorize("hasRole('EMPLOYER') or hasRole('ADMIN')")
  public ResponseEntity<CompanyMemberResponse> toggleMemberStatus(@PathVariable Long id) {
    log.info("Toggling status for team member: {}", id);
    CompanyMemberResponse response = memberService.toggleMemberStatus(id);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/team-members/{id}")
  @PreAuthorize("hasRole('EMPLOYER') or hasRole('ADMIN')")
  public ResponseEntity<Void> removeTeamMember(@PathVariable Long id) {
    log.info("Removing team member: {}", id);
    memberService.removeMember(id);
    return ResponseEntity.noContent().build();
  }

  // =============== STATISTICS ENDPOINTS ===============

  @GetMapping("/stats")
  @PreAuthorize("hasRole('EMPLOYER') or hasRole('ADMIN')")
  public ResponseEntity<Map<String, Object>> getCompanyAdminStats(@RequestParam Long companyId) {
    log.info("Getting company admin stats for company: {}", companyId);

    TeamStatsResponse teamStats = memberService.getTeamStats(companyId);
    long pendingRequests = joinRequestService.countPendingRequestsByCompany(companyId);

    teamStats.setPendingRequests(pendingRequests);

    Map<String, Object> stats = Map.of(
        "teamStats", teamStats,
        "companyId", companyId);

    return ResponseEntity.ok(stats);
  }

  @GetMapping("/team-stats")
  @PreAuthorize("hasRole('EMPLOYER') or hasRole('ADMIN')")
  public ResponseEntity<TeamStatsResponse> getTeamStats(@RequestParam Long companyId) {
    log.info("Getting team stats for company: {}", companyId);
    TeamStatsResponse stats = memberService.getTeamStats(companyId);
    long pendingRequests = joinRequestService.countPendingRequestsByCompany(companyId);
    stats.setPendingRequests(pendingRequests);
    return ResponseEntity.ok(stats);
  }

  // =============== UTILITY ENDPOINTS ===============

  @GetMapping("/check-membership")
  @PreAuthorize("hasRole('CANDIDATE') or hasRole('EMPLOYER') or hasRole('ADMIN')")
  public ResponseEntity<Map<String, Object>> checkUserMembership(
      @RequestParam Long userId,
      @RequestParam Long companyId) {

    log.info("Checking membership for user: {} in company: {}", userId, companyId);

    boolean isMember = memberService.isUserMemberOfCompany(userId, companyId);
    boolean isAdmin = memberService.isUserAdminOfCompany(userId, companyId);
    boolean hasExistingRequest = joinRequestService.hasExistingRequest(userId, companyId);

    Map<String, Object> result = Map.of(
        "isMember", isMember,
        "isAdmin", isAdmin,
        "hasExistingRequest", hasExistingRequest,
        "userId", userId,
        "companyId", companyId);

    return ResponseEntity.ok(result);
  }
}