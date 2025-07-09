package com.jobhunt.service;

import com.jobhunt.model.request.UserContactInfoRequest;
import com.jobhunt.model.request.UserInfoRequest;
import com.jobhunt.model.request.UserProfileSectionRequest;
import com.jobhunt.model.request.UserSocialNetworkRequest;
import com.jobhunt.model.response.UserInfoResponse;
import org.springframework.web.multipart.MultipartFile;

public interface UserInfoService {

  /**
   * Get user profile information
   * 
   * @param userId The user ID
   * @return UserInfoResponse or null if not found
   */
  UserInfoResponse getUserProfile(Long userId);

  /**
   * Check if user has profile information
   * 
   * @param userId The user ID
   * @return true if profile exists, false otherwise
   */
  boolean hasProfile(Long userId);

  /**
   * Create initial profile (first time save)
   * 
   * @param userId  The user ID
   * @param request The complete profile data
   * @return Created UserInfoResponse
   */
  UserInfoResponse createProfile(Long userId, UserInfoRequest request);

  /**
   * Update complete profile (full update)
   * 
   * @param userId  The user ID
   * @param request The complete profile data
   * @return Updated UserInfoResponse
   */
  UserInfoResponse updateCompleteProfile(Long userId, UserInfoRequest request);

  /**
   * Update main profile section only
   * 
   * @param userId  The user ID
   * @param request The profile section data
   * @return Updated UserInfoResponse
   */
  UserInfoResponse updateProfileSection(Long userId, UserProfileSectionRequest request);

  /**
   * Update social network section only
   * 
   * @param userId  The user ID
   * @param request The social network data
   * @return Updated UserInfoResponse
   */
  UserInfoResponse updateSocialNetworkSection(Long userId, UserSocialNetworkRequest request);

  /**
   * Update contact information section only
   * 
   * @param userId  The user ID
   * @param request The contact info data
   * @return Updated UserInfoResponse
   */
  UserInfoResponse updateContactInfoSection(Long userId, UserContactInfoRequest request);

  /**
   * Upload and update profile picture
   * 
   * @param userId The user ID
   * @param file   The profile picture file
   * @return Updated UserInfoResponse
   */
  UserInfoResponse updateProfilePicture(Long userId, MultipartFile file);

  /**
   * Delete user profile
   * 
   * @param userId The user ID
   */
  void deleteProfile(Long userId);

  /**
   * Calculate and update profile completion percentage
   * 
   * @param userId The user ID
   * @return Updated completion percentage
   */
  Integer updateProfileCompletion(Long userId);
}