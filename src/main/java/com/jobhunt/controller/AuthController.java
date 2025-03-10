package com.jobhunt.controller;

import com.jobhunt.model.request.LoginRequest;
import com.jobhunt.model.response.LoginResponse;
import com.jobhunt.payload.Response;
import com.jobhunt.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        var response = authService.login(loginRequest);
        ResponseCookie refreshCookie = authService.createRefreshCookie(response.getAccessToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(response);
    }

    @GetMapping("/account")
    public ResponseEntity<?> getAccount() {
        return ResponseEntity.ok(Response.ofSucceeded(authService.getAccount()));
    }

    @GetMapping("/refresh")
    public ResponseEntity<?> refresh(@CookieValue(name = "refreshToken") String refreshToken) {
        var response = authService.refresh(refreshToken);
        var newRefreshCookie = authService.createRefreshCookie(response.getAccessToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, newRefreshCookie.toString())
                .body(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        authService.logout();

        var deleteCookie = authService.createDeleteCookie();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                .body("Successfully logged out");
    }
}
