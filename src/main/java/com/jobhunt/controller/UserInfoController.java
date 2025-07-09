package com.jobhunt.controller;

import com.jobhunt.exception.BadRequestException;
import com.jobhunt.model.request.UserContactInfoRequest;
import com.jobhunt.model.request.UserInfoRequest;
import com.jobhunt.model.request.UserProfileSectionRequest;
import com.jobhunt.model.request.UserSocialNetworkRequest;
import com.jobhunt.payload.Response;
import com.jobhunt.service.CompanyAuthorizationService;
import com.jobhunt.service.UserInfoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/user-info")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class UserInfoController {

  private final UserInfoService userInfoService;
  private final CompanyAuthorizationService authorizationService;

  @GetMapping
  @PreAuthorize("hasRole('CANDIDATE')")
  public ResponseEntity<?> getUserProfile(@RequestParam(required = false) Long userId) {
    // If userId not provided, use current user
    Long targetUserId = userId != null ? userId : authorizationService.getCurrentUserId();

    if (targetUserId == null) {
      throw new IllegalArgumentException("User ID is required");
    }

    log.info("Getting user profile for user: {}", targetUserId);

    var userProfile = userInfoService.getUserProfile(targetUserId);
    boolean hasProfile = userInfoService.hasProfile(targetUserId);

    var result = java.util.Map.of(
        "userProfile", userProfile,
        "hasProfile", hasProfile,
        "isFirstTime", !hasProfile);

    return ResponseEntity.ok(Response.ofSucceeded(result));
  }

  @PostMapping
  @PreAuthorize("hasRole('CANDIDATE')")
  public ResponseEntity<?> createProfile(@Valid @RequestBody UserInfoRequest request) {
    Long currentUserId = authorizationService.getCurrentUserId();
    if (currentUserId == null) {
      throw new BadRequestException("Authentication required");
    }

    log.info("Creating profile for user: {}", currentUserId);

    try {
      var createdProfile = userInfoService.createProfile(currentUserId, request);
      return ResponseEntity.status(HttpStatus.CREATED)
          .body(Response.ofSucceeded(createdProfile));
    } catch (Exception e) {
      log.error("Error creating profile for user {}: {}", currentUserId, e.getMessage());
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
  }

  @PutMapping
  @PreAuthorize("hasRole('CANDIDATE')")
  public ResponseEntity<?> updateCompleteProfile(@Valid @RequestBody UserInfoRequest request) {
    Long currentUserId = authorizationService.getCurrentUserId();
    if (currentUserId == null) {
      throw new BadRequestException("Authentication required");
    }

    log.info("Updating complete profile for user: {}", currentUserId);

    try {
      var updatedProfile = userInfoService.updateCompleteProfile(currentUserId, request);
      return ResponseEntity.ok(Response.ofSucceeded(updatedProfile));
    } catch (Exception e) {
      log.error("Error updating complete profile for user {}: {}", currentUserId, e.getMessage());
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
  }

  @PatchMapping("/profile-section")
  @PreAuthorize("hasRole('CANDIDATE')")
  public ResponseEntity<?> updateProfileSection(@Valid @RequestBody UserProfileSectionRequest request) {
    Long currentUserId = authorizationService.getCurrentUserId();
    if (currentUserId == null) {
      throw new BadRequestException("Authentication required");
    }

    log.info("Updating profile section for user: {}", currentUserId);

    try {
      var updatedProfile = userInfoService.updateProfileSection(currentUserId, request);
      return ResponseEntity.ok(Response.ofSucceeded(updatedProfile));
    } catch (Exception e) {
      log.error("Error updating profile section for user {}: {}", currentUserId, e.getMessage());
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
  }

  @PatchMapping("/social-network")
  @PreAuthorize("hasRole('CANDIDATE')")
  public ResponseEntity<?> updateSocialNetworkSection(@Valid @RequestBody UserSocialNetworkRequest request) {
    Long currentUserId = authorizationService.getCurrentUserId();
    if (currentUserId == null) {
      throw new BadRequestException("Authentication required");
    }

    log.info("Updating social network section for user: {}", currentUserId);

    try {
      var updatedProfile = userInfoService.updateSocialNetworkSection(currentUserId, request);
      return ResponseEntity.ok(Response.ofSucceeded(updatedProfile));
    } catch (Exception e) {
      log.error("Error updating social network section for user {}: {}", currentUserId, e.getMessage());
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
  }

  @PatchMapping("/contact-info")
  @PreAuthorize("hasRole('CANDIDATE')")
  public ResponseEntity<?> updateContactInfoSection(@Valid @RequestBody UserContactInfoRequest request) {
    Long currentUserId = authorizationService.getCurrentUserId();
    if (currentUserId == null) {
      throw new BadRequestException("Authentication required");
    }

    log.info("Updating contact info section for user: {}", currentUserId);

    try {
      var updatedProfile = userInfoService.updateContactInfoSection(currentUserId, request);
      return ResponseEntity.ok(Response.ofSucceeded(updatedProfile));
    } catch (Exception e) {
      log.error("Error updating contact info section for user {}: {}", currentUserId, e.getMessage());
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
  }

  @PostMapping("/profile-picture")
  @PreAuthorize("hasRole('CANDIDATE')")
  public ResponseEntity<?> updateProfilePicture(@RequestParam("file") MultipartFile file) {
    Long currentUserId = authorizationService.getCurrentUserId();
    if (currentUserId == null) {
      throw new BadRequestException("Authentication required");
    }

    log.info("Updating profile picture for user: {}", currentUserId);

    try {
      var updatedProfile = userInfoService.updateProfilePicture(currentUserId, file);
      return ResponseEntity.ok(Response.ofSucceeded(updatedProfile));
    } catch (Exception e) {
      log.error("Error updating profile picture for user {}: {}", currentUserId, e.getMessage());
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
  }

  @DeleteMapping
  @PreAuthorize("hasRole('CANDIDATE')")
  public ResponseEntity<?> deleteProfile() {
    Long currentUserId = authorizationService.getCurrentUserId();
    if (currentUserId == null) {
      throw new BadRequestException("Authentication required");
    }

    log.info("Deleting profile for user: {}", currentUserId);

    try {
      userInfoService.deleteProfile(currentUserId);
      return ResponseEntity.ok(Response.ofSucceeded("Profile deleted successfully"));
    } catch (Exception e) {
      log.error("Error deleting profile for user {}: {}", currentUserId, e.getMessage());
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
  }

  @PostMapping("/update-completion")
  @PreAuthorize("hasRole('CANDIDATE')")
  public ResponseEntity<?> updateProfileCompletion() {
    Long currentUserId = authorizationService.getCurrentUserId();
    if (currentUserId == null) {
      throw new BadRequestException("Authentication required");
    }

    log.info("Updating profile completion for user: {}", currentUserId);

    try {
      Integer completionPercentage = userInfoService.updateProfileCompletion(currentUserId);
      var result = java.util.Map.of(
          "userId", currentUserId,
          "completionPercentage", completionPercentage);
      return ResponseEntity.ok(Response.ofSucceeded(result));
    } catch (Exception e) {
      log.error("Error updating profile completion for user {}: {}", currentUserId, e.getMessage());
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
  }
}