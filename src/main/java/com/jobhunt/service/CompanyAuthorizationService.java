package com.jobhunt.service;

public interface CompanyAuthorizationService {

  /**
   * Check if the current authenticated user is an admin of the specified company
   * 
   * @param companyId The ID of the company to check
   * @return true if current user is admin of the company, false otherwise
   */
  boolean isCurrentUserAdminOfCompany(Long companyId);

  /**
   * Check if the specified user is an admin of the specified company
   * 
   * @param userId    The ID of the user to check
   * @param companyId The ID of the company to check
   * @return true if user is admin of the company, false otherwise
   */
  boolean isUserAdminOfCompany(Long userId, Long companyId);

  /**
   * Get the current authenticated user's ID from security context
   * 
   * @return The current user's ID
   */
  Long getCurrentUserId();

  /**
   * Check if current user can access join request (admin of the company that the
   * request belongs to)
   * 
   * @param joinRequestId The ID of the join request
   * @return true if current user can access, false otherwise
   */
  boolean canAccessJoinRequest(Long joinRequestId);

  /**
   * Check if current user can access team member (admin of the same company)
   * 
   * @param memberId The ID of the team member
   * @return true if current user can access, false otherwise
   */
  boolean canAccessTeamMember(Long memberId);

  /**
   * Validate that current user is admin of company or throw exception
   * 
   * @param companyId The company ID to validate
   */
  void validateAdminAccess(Long companyId);
}