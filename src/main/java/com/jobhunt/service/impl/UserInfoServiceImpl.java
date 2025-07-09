package com.jobhunt.service.impl;

import com.jobhunt.exception.BadRequestException;
import com.jobhunt.exception.ResourceNotFoundException;
import com.jobhunt.mapper.UserInfoMapper;
import com.jobhunt.model.entity.User;
import com.jobhunt.model.entity.UserInfo;
import com.jobhunt.model.request.UserContactInfoRequest;
import com.jobhunt.model.request.UserInfoRequest;
import com.jobhunt.model.request.UserProfileSectionRequest;
import com.jobhunt.model.request.UserSocialNetworkRequest;
import com.jobhunt.model.response.UserInfoResponse;
import com.jobhunt.repository.UserInfoRepository;
import com.jobhunt.repository.UserRepository;
import com.jobhunt.service.UserInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserInfoServiceImpl implements UserInfoService {

  private final UserInfoRepository userInfoRepository;
  private final UserRepository userRepository;
  private final UserInfoMapper mapper;

  @Override
  @Transactional(readOnly = true)
  public UserInfoResponse getUserProfile(Long userId) {
    log.info("Getting user profile for user: {}", userId);

    return userInfoRepository.findByUserId(userId)
        .map(mapper::toResponse)
        .orElse(null);
  }

  @Override
  @Transactional(readOnly = true)
  public boolean hasProfile(Long userId) {
    log.info("Checking if user {} has profile", userId);
    return userInfoRepository.existsByUserId(userId);
  }

  @Override
  public UserInfoResponse createProfile(Long userId, UserInfoRequest request) {
    log.info("Creating profile for user: {}", userId);

    // Check if profile already exists
    if (userInfoRepository.existsByUserId(userId)) {
      throw new BadRequestException("User profile already exists. Use update endpoint instead.");
    }

    // Get user entity
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

    // Create new UserInfo
    UserInfo userInfo = mapper.toEntity(request);
    userInfo.setUser(user);

    // Calculate completion percentage
    userInfo.updateCompletionPercentage();

    // Save and return
    UserInfo savedUserInfo = userInfoRepository.save(userInfo);
    log.info("Profile created successfully for user: {} with completion: {}%",
        userId, savedUserInfo.getCompletionPercentage());

    return mapper.toResponse(savedUserInfo);
  }

  @Override
  public UserInfoResponse updateCompleteProfile(Long userId, UserInfoRequest request) {
    log.info("Updating complete profile for user: {}", userId);

    UserInfo userInfo = userInfoRepository.findByUserId(userId)
        .orElseThrow(() -> new ResourceNotFoundException("User profile not found for user: " + userId));

    // Update all fields
    mapper.updateEntityFromRequest(request, userInfo);

    // Recalculate completion percentage
    userInfo.updateCompletionPercentage();

    UserInfo savedUserInfo = userInfoRepository.save(userInfo);
    log.info("Complete profile updated for user: {} with completion: {}%",
        userId, savedUserInfo.getCompletionPercentage());

    return mapper.toResponse(savedUserInfo);
  }

  @Override
  public UserInfoResponse updateProfileSection(Long userId, UserProfileSectionRequest request) {
    log.info("Updating profile section for user: {}", userId);

    UserInfo userInfo = userInfoRepository.findByUserId(userId)
        .orElseThrow(() -> new ResourceNotFoundException("User profile not found for user: " + userId));

    // Update only profile section fields
    mapper.updateEntityFromProfileSection(request, userInfo);

    // Recalculate completion percentage
    userInfo.updateCompletionPercentage();

    UserInfo savedUserInfo = userInfoRepository.save(userInfo);
    log.info("Profile section updated for user: {} with completion: {}%",
        userId, savedUserInfo.getCompletionPercentage());

    return mapper.toResponse(savedUserInfo);
  }

  @Override
  public UserInfoResponse updateSocialNetworkSection(Long userId, UserSocialNetworkRequest request) {
    log.info("Updating social network section for user: {}", userId);

    UserInfo userInfo = userInfoRepository.findByUserId(userId)
        .orElseThrow(() -> new ResourceNotFoundException("User profile not found for user: " + userId));

    // Update only social network fields
    mapper.updateEntityFromSocialNetwork(request, userInfo);

    // Recalculate completion percentage
    userInfo.updateCompletionPercentage();

    UserInfo savedUserInfo = userInfoRepository.save(userInfo);
    log.info("Social network section updated for user: {} with completion: {}%",
        userId, savedUserInfo.getCompletionPercentage());

    return mapper.toResponse(savedUserInfo);
  }

  @Override
  public UserInfoResponse updateContactInfoSection(Long userId, UserContactInfoRequest request) {
    log.info("Updating contact info section for user: {}", userId);

    UserInfo userInfo = userInfoRepository.findByUserId(userId)
        .orElseThrow(() -> new ResourceNotFoundException("User profile not found for user: " + userId));

    // Update only contact info fields
    mapper.updateEntityFromContactInfo(request, userInfo);

    // Recalculate completion percentage
    userInfo.updateCompletionPercentage();

    UserInfo savedUserInfo = userInfoRepository.save(userInfo);
    log.info("Contact info section updated for user: {} with completion: {}%",
        userId, savedUserInfo.getCompletionPercentage());

    return mapper.toResponse(savedUserInfo);
  }

  @Override
  public UserInfoResponse updateProfilePicture(Long userId, MultipartFile file) {
    log.info("Updating profile picture for user: {}", userId);

    if (file == null || file.isEmpty()) {
      throw new BadRequestException("Profile picture file is required");
    }

    UserInfo userInfo = userInfoRepository.findByUserId(userId)
        .orElseThrow(() -> new ResourceNotFoundException("User profile not found for user: " + userId));

    try {
      // Recalculate completion percentage
      userInfo.updateCompletionPercentage();

      UserInfo savedUserInfo = userInfoRepository.save(userInfo);
      log.info("Profile picture updated for user: {}", userId);

      return mapper.toResponse(savedUserInfo);
    } catch (Exception e) {
      log.error("Error uploading profile picture for user {}: {}", userId, e.getMessage());
      throw new BadRequestException("Failed to upload profile picture: " + e.getMessage());
    }
  }

  @Override
  public void deleteProfile(Long userId) {
    log.info("Deleting profile for user: {}", userId);

    UserInfo userInfo = userInfoRepository.findByUserId(userId)
        .orElseThrow(() -> new ResourceNotFoundException("User profile not found for user: " + userId));

    userInfoRepository.delete(userInfo);
    log.info("Profile deleted successfully for user: {}", userId);
  }

  @Override
  public Integer updateProfileCompletion(Long userId) {
    log.info("Updating profile completion for user: {}", userId);

    UserInfo userInfo = userInfoRepository.findByUserId(userId)
        .orElseThrow(() -> new ResourceNotFoundException("User profile not found for user: " + userId));

    userInfo.updateCompletionPercentage();
    UserInfo savedUserInfo = userInfoRepository.save(userInfo);

    log.info("Profile completion updated for user: {} to {}%", userId, savedUserInfo.getCompletionPercentage());
    return savedUserInfo.getCompletionPercentage();
  }
}