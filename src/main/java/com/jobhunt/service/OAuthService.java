package com.jobhunt.service;

import com.jobhunt.model.response.AuthResponse;

public interface OAuthService {
  /**
   * Generate Google OAuth authorization URL
   * 
   * @return Google OAuth authorization URL
   */
  String getGoogleAuthUrl();

  /**
   * Handle OAuth callback and exchange code for tokens
   * 
   * @param code Authorization code from OAuth provider
   * @return AuthResponse with user data and tokens
   */
  AuthResponse handleOAuthCallback(String code);
}