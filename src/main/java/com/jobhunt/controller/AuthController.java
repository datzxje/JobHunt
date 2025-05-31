package com.jobhunt.controller;

import com.jobhunt.model.request.ChangePasswordRequest;
import com.jobhunt.model.request.LoginRequest;
import com.jobhunt.model.request.SignUpRequest;
import com.jobhunt.payload.Response;
import com.jobhunt.service.AuthService;
import com.jobhunt.exception.BadRequestException;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  @PostMapping("/login")
  public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
    return ResponseEntity.ok(Response.ofSucceeded(authService.login(request)));
  }

  @PostMapping("/signup")
  public ResponseEntity<?> signup(@Valid @RequestBody SignUpRequest request) {
    return ResponseEntity.ok(Response.ofSucceeded(authService.signup(request)));
  }

  @PostMapping("/logout")
  public ResponseEntity<?> logout() {
    try {
      authService.logout();
    } catch (Exception e) {
      // Vẫn xóa cookie ngay cả khi không tìm thấy user
    }
    return ResponseEntity.ok(Response.ofSucceeded());
  }

  @PostMapping("/refresh-token")
  public ResponseEntity<?> refreshToken() {
    return ResponseEntity.ok(Response.ofSucceeded(authService.refreshToken()));
  }

  /**
   * Endpoint để lấy thông tin người dùng hiện tại từ token
   * 
   * @return thông tin người dùng hoặc 401 Unauthorized nếu không xác thực
   */
  @GetMapping("/me")
  public ResponseEntity<?> getCurrentUser() {
    return ResponseEntity.ok(Response.ofSucceeded(authService.getCurrentUser()));
  }

  @PostMapping("/change-password")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<?> changePassword(@RequestBody @Valid ChangePasswordRequest request) {
    String userId = SecurityContextHolder.getContext().getAuthentication().getName();
    authService.changePassword(Long.parseLong(userId), request);
    return ResponseEntity.ok(Response.ofSucceeded());
  }

  @PostMapping("/reset-password")
  public ResponseEntity<?> resetPassword(@RequestParam String email) {
    authService.resetPassword(email);
    return ResponseEntity.ok(Response.ofSucceeded());
  }

  @PostMapping("/reset-password/confirm")
  public ResponseEntity<?> confirmResetPassword(
      @RequestParam String token,
      @RequestParam String newPassword) {
    authService.confirmResetPassword(token, newPassword);
    return ResponseEntity.ok(Response.ofSucceeded());
  }
}