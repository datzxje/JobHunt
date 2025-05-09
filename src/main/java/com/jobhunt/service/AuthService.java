package com.jobhunt.service;

import com.jobhunt.model.request.ChangePasswordRequest;
import com.jobhunt.model.request.LoginRequest;
import com.jobhunt.model.request.SignUpRequest;
import com.jobhunt.model.response.AuthResponse;

public interface AuthService {
  AuthResponse login(LoginRequest request);

  AuthResponse signup(SignUpRequest request);

  void logout(String refreshToken);

  AuthResponse refreshToken(String refreshToken);

  void changePassword(Long userId, ChangePasswordRequest request);

  void resetPassword(String email);

  void confirmResetPassword(String token, String newPassword);
}