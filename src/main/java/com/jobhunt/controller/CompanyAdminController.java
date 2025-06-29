package com.jobhunt.controller;

import com.jobhunt.model.entity.CompanyJoinRequest;
import com.jobhunt.model.entity.CompanyMember;
import com.jobhunt.model.request.CompanyJoinRequestRequest;
import com.jobhunt.model.request.CompanyMemberUpdateRequest;
import com.jobhunt.model.response.CompanyJoinRequestResponse;
import com.jobhunt.model.response.CompanyMemberResponse;
import com.jobhunt.model.response.TeamStatsResponse;
import com.jobhunt.payload.Response;
import com.jobhunt.service.CompanyAuthorizationService;
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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/company-admin")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class CompanyAdminController {

  private final CompanyJoinRequestService joinRequestService;
  private final CompanyMemberService memberService;
  private final CompanyAuthorizationService authorizationService;

  @GetMapping("/join-requests")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> getJoinRequests(
      @RequestParam Long companyId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(required = false) String status) {

    log.info("Getting join requests for company: {}, page: {}, size: {}, status: {}",
        companyId, page, size, status);

    authorizationService.validateAdminAccess(companyId);

    Pageable pageable = PageRequest.of(page, size);
    Page<CompanyJoinRequestResponse> requests;

    if (status != null && !status.isEmpty()) {
      CompanyJoinRequest.RequestStatus requestStatus = CompanyJoinRequest.RequestStatus.valueOf(status.toUpperCase());
      requests = joinRequestService.getJoinRequestsByCompanyAndStatus(companyId, requestStatus, pageable);
    } else {
      requests = joinRequestService.getJoinRequestsByCompany(companyId, pageable);
    }

    return ResponseEntity.ok(Response.ofSucceeded(requests));
  }

  @GetMapping("/join-requests/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> getJoinRequestById(@PathVariable Long id) {
    log.info("Getting join request by id: {}", id);

    // Validate admin access to this join request
    if (!authorizationService.canAccessJoinRequest(id)) {
      throw new AccessDeniedException("You are not authorized to access this join request");
    }

    return ResponseEntity.ok(Response.ofSucceeded(joinRequestService.getJoinRequestById(id)));
  }

  @PostMapping("/join-requests")
  @PreAuthorize("hasRole('EMPLOYER')")
  public ResponseEntity<?> createJoinRequest(
      @Valid @RequestBody CompanyJoinRequestRequest request,
      @RequestParam Long userId) {

    log.info("Creating join request for user: {} to company: {}", userId, request.getCompanyId());

    // Note: For creating join requests, we don't need company admin validation
    // as this is for employers requesting to join a company

    return ResponseEntity.ok(Response.ofSucceeded(joinRequestService.createJoinRequest(request, userId)));
  }

  @PutMapping("/join-requests/{id}/approve")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> approveJoinRequest(
      @PathVariable Long id,
      @RequestParam Long reviewerId) {

    log.info("Approving join request: {} by reviewer: {}", id, reviewerId);

    // Validate admin access to this join request
    if (!authorizationService.canAccessJoinRequest(id)) {
      throw new AccessDeniedException("You are not authorized to approve this join request");
    }

    return ResponseEntity.ok(Response.ofSucceeded(joinRequestService.approveJoinRequest(id, reviewerId)));
  }

  @PutMapping("/join-requests/{id}/reject")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> rejectJoinRequest(
      @PathVariable Long id,
      @RequestParam Long reviewerId) {

    log.info("Rejecting join request: {} by reviewer: {}", id, reviewerId);

    // Validate admin access to this join request
    if (!authorizationService.canAccessJoinRequest(id)) {
      throw new AccessDeniedException("You are not authorized to reject this join request");
    }

    return ResponseEntity.ok(Response.ofSucceeded(joinRequestService.rejectJoinRequest(id, reviewerId)));
  }

  @DeleteMapping("/join-requests/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> deleteJoinRequest(@PathVariable Long id) {
    log.info("Deleting join request: {}", id);

    // Validate admin access to this join request
    if (!authorizationService.canAccessJoinRequest(id)) {
      throw new AccessDeniedException("You are not authorized to delete this join request");
    }

    joinRequestService.deleteJoinRequest(id);
    return ResponseEntity.noContent().build();
  }

  // =============== TEAM MEMBERS ENDPOINTS ===============

  @GetMapping("/team-members")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> getTeamMembers(
      @RequestParam Long companyId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(required = false) String role,
      @RequestParam(required = false) String status) {

    log.info("Getting team members for company: {}, page: {}, size: {}, role: {}, status: {}",
        companyId, page, size, role, status);

    // Validate admin access to company
    authorizationService.validateAdminAccess(companyId);

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

    return ResponseEntity.ok(Response.ofSucceeded(members));
  }

  @GetMapping("/team-members/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> getTeamMemberById(@PathVariable Long id) {
    log.info("Getting team member by id: {}", id);

    // Validate admin access to this team member
    if (!authorizationService.canAccessTeamMember(id)) {
      throw new AccessDeniedException("You are not authorized to access this team member");
    }

    return ResponseEntity.ok(Response.ofSucceeded(memberService.getMemberById(id)));
  }

  @PostMapping("/team-members")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> addTeamMember(
      @RequestParam Long userId,
      @RequestParam Long companyId,
      @RequestParam(defaultValue = "HR") String role) {

    log.info("Adding team member: {} to company: {} with role: {}", userId, companyId, role);

    // Validate admin access to company
    authorizationService.validateAdminAccess(companyId);

    CompanyMember.MemberRole memberRole = CompanyMember.MemberRole.valueOf(role.toUpperCase());
    return ResponseEntity.ok(Response.ofSucceeded(memberService.addMember(userId, companyId, memberRole)));
  }

  @PutMapping("/team-members/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> updateTeamMember(
      @PathVariable Long id,
      @Valid @RequestBody CompanyMemberUpdateRequest request) {

    log.info("Updating team member: {}", id);

    // Validate admin access to this team member
    if (!authorizationService.canAccessTeamMember(id)) {
      throw new AccessDeniedException("You are not authorized to update this team member");
    }

    return ResponseEntity.ok(Response.ofSucceeded(memberService.updateMember(id, request)));
  }

  @PutMapping("/team-members/{id}/transfer-admin")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> transferAdminRights(
      @PathVariable Long id,
      @RequestParam Long currentAdminId,
      @RequestParam Long companyId) {

    log.info("Transferring admin rights from: {} to: {} for company: {}", currentAdminId, id, companyId);

    // Validate admin access to company
    authorizationService.validateAdminAccess(companyId);

    // Additional validation: ensure current user is the current admin
    Long currentUserId = authorizationService.getCurrentUserId();
    if (currentUserId == null || !currentUserId.equals(currentAdminId)) {
      throw new AccessDeniedException("You can only transfer your own admin rights");
    }

    return ResponseEntity.ok(Response.ofSucceeded(memberService.transferAdminRights(currentAdminId, id, companyId)));
  }

  @PutMapping("/team-members/{id}/toggle-status")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> toggleMemberStatus(@PathVariable Long id) {
    log.info("Toggling status for team member: {}", id);

    // Validate admin access to this team member
    if (!authorizationService.canAccessTeamMember(id)) {
      throw new AccessDeniedException("You are not authorized to modify this team member");
    }

    return ResponseEntity.ok(Response.ofSucceeded(memberService.toggleMemberStatus(id)));
  }

  @DeleteMapping("/team-members/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> removeTeamMember(@PathVariable Long id) {
    log.info("Removing team member: {}", id);

    // Validate admin access to this team member
    if (!authorizationService.canAccessTeamMember(id)) {
      throw new AccessDeniedException("You are not authorized to remove this team member");
    }

    memberService.removeMember(id);
    return ResponseEntity.noContent().build();
  }

  // =============== STATISTICS ENDPOINTS ===============

  @GetMapping("/stats")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> getCompanyAdminStats(@RequestParam Long companyId) {
    log.info("Getting company admin stats for company: {}", companyId);

    // Validate admin access to company
    authorizationService.validateAdminAccess(companyId);

    TeamStatsResponse teamStats = memberService.getTeamStats(companyId);
    long pendingRequests = joinRequestService.countPendingRequestsByCompany(companyId);

    teamStats.setPendingRequests(pendingRequests);

    Map<String, Object> stats = Map.of(
        "teamStats", teamStats,
        "companyId", companyId);

    return ResponseEntity.ok(Response.ofSucceeded(stats));
  }

  @GetMapping("/team-stats")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> getTeamStats(@RequestParam Long companyId) {
    log.info("Getting team stats for company: {}", companyId);

    // Validate admin access to company
    authorizationService.validateAdminAccess(companyId);

    TeamStatsResponse stats = memberService.getTeamStats(companyId);
    long pendingRequests = joinRequestService.countPendingRequestsByCompany(companyId);
    stats.setPendingRequests(pendingRequests);
    return ResponseEntity.ok(Response.ofSucceeded(stats));
  }

  // =============== UTILITY ENDPOINTS ===============

  @GetMapping("/my-companies")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> getMyManagedCompanies() {
    Long currentUserId = authorizationService.getCurrentUserId();
    if (currentUserId == null) {
      throw new AccessDeniedException("Authentication required");
    }

    log.info("Getting companies managed by user: {}", currentUserId);

    // Get companies where user is an active admin
    List<CompanyMemberResponse> adminMemberships = memberService.getAdminMemberships(currentUserId);

    return ResponseEntity.ok(Response.ofSucceeded(adminMemberships));
  }

  @GetMapping("/check-membership")
  @PreAuthorize("hasRole('CANDIDATE') or hasRole('EMPLOYER') or hasRole('ADMIN')")
  public ResponseEntity<?> checkUserMembership(
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

    return ResponseEntity.ok(Response.ofSucceeded(result));
  }
}