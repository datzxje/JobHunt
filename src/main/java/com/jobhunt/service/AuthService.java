package com.jobhunt.service;

import com.jobhunt.model.request.ChangePasswordRequest;
import com.jobhunt.model.request.LoginRequest;
import com.jobhunt.model.request.SignUpRequest;
import com.jobhunt.model.response.AuthResponse;
import com.jobhunt.model.response.UserResponse;

public interface AuthService {
  AuthResponse login(LoginRequest request);

  AuthResponse signup(SignUpRequest request);

  void logout();

  AuthResponse refreshToken();

  void changePassword(Long userId, ChangePasswordRequest request);

  void resetPassword(String email);

  void confirmResetPassword(String token, String newPassword);

  /**
   * Lấy thông tin người dùng hiện tại từ access token
   * 
   * @return thông tin người dùng hoặc null nếu không xác thực
   */
  UserResponse getCurrentUser();
}