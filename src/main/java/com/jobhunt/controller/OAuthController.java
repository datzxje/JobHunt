package com.jobhunt.controller;

import com.jobhunt.payload.Response;
import com.jobhunt.service.OAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth/oauth")
@RequiredArgsConstructor
@Slf4j
public class OAuthController {

  private final OAuthService oauthService;

  /**
   * Get Google OAuth authorization URL
   * 
   * @return Google OAuth URL for frontend to redirect to
   */
  @GetMapping("/google/url")
  public ResponseEntity<?> getGoogleAuthUrl() {
    try {
      log.info("Generating Google OAuth URL");
      String authUrl = oauthService.getGoogleAuthUrl();
      log.info("Generated Google OAuth URL: {}", authUrl);

      return ResponseEntity.ok(Response.ofSucceeded(Map.of("authUrl", authUrl)));
    } catch (Exception e) {
      log.error("Failed to generate Google OAuth URL: {}", e.getMessage(), e);
      return ResponseEntity.badRequest()
          .body(Response.ofSucceeded("Failed to generate OAuth URL"));
    }
  }

  /**
   * Handle OAuth callback from provider
   * 
   * @param code  Authorization code from OAuth provider
   * @param error Error from OAuth provider (if any)
   * @param state State parameter for CSRF protection
   * @return AuthResponse with user data and tokens
   */
  @GetMapping("/callback")
  public ResponseEntity<?> handleOAuthCallback(
      @RequestParam String code,
      @RequestParam(required = false) String error,
      @RequestParam(required = false) String state) {

    log.info("Received OAuth callback - code: {}, error: {}, state: {}",
        code.substring(0, Math.min(10, code.length())) + "...",
        error, state);

    if (error != null) {
      log.error("OAuth error received: {}", error);
      return ResponseEntity.badRequest()
          .body(Response.ofSucceeded("OAuth authentication failed: " + error));
    }

    if (code == null || code.trim().isEmpty()) {
      log.error("No authorization code received in OAuth callback");
      return ResponseEntity.badRequest()
          .body(Response.ofSucceeded("No authorization code received"));
    }

    try {
      log.info("Processing OAuth callback with code");
      var authResponse = oauthService.handleOAuthCallback(code);
      log.info("OAuth callback processed successfully");

      return ResponseEntity.ok(Response.ofSucceeded(authResponse));
    } catch (Exception e) {
      log.error("OAuth callback processing failed: {}", e.getMessage(), e);
      return ResponseEntity.badRequest()
          .body(Response.ofSucceeded("OAuth callback processing failed: " + e.getMessage()));
    }
  }
}