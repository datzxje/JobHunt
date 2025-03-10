package com.jobhunt.service.impl;

import com.jobhunt.model.request.LoginRequest;
import com.jobhunt.model.response.LoginResponse;
import com.jobhunt.repository.UserRepository;
import com.jobhunt.service.AuthService;
import com.jobhunt.service.UserService;
import com.jobhunt.util.SecurityUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final SecurityUtil securityUtil;
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;

    @Value("${jwt.refresh-token-validity-time}")
    private Long refreshTokenExpiredTime;

    @Transactional
    public LoginResponse login(LoginRequest loginRequest) {
        var authToken = new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword());

        var authentication = authenticationManager.authenticate(authToken);
        var sc = SecurityContextHolder.getContext();
        sc.setAuthentication(authentication);

        var currentUser = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        var res = new LoginResponse();

        if (currentUser != null) {
            var userLogin = new LoginResponse.UserLogin(
                    currentUser.getId(), currentUser.getUsername(), currentUser.getEmail(),
                    currentUser.getFirstname(), currentUser.getLastname());
            res.setUser(userLogin);
        }

        var accessToken = securityUtil.generateAccessToken(authentication.getName(), res.getUser());
        var refreshToken = securityUtil.generateRefreshToken(loginRequest.getEmail(), res.getUser());

        res.setAccessToken(accessToken);

        userService.updateUserToken(refreshToken, loginRequest.getEmail());

        return res;
    }

    public ResponseCookie createRefreshCookie(String refreshToken) {
        return ResponseCookie
                .from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("Strict")
                .maxAge(refreshTokenExpiredTime)
                .build();
    }

    public LoginResponse refresh(String refreshToken) {
        var token = securityUtil.checkValidRefreshToken(refreshToken);
        String email = token.getSubject();

        var currentUser = userRepository.findByRefreshTokenAndEmail(refreshToken, email)
                .orElseThrow(() -> new RuntimeException("Invalid Refresh Token"));

        var res = new LoginResponse();

        var userLogin = new LoginResponse.UserLogin(
                currentUser.getId(), currentUser.getUsername(), currentUser.getEmail(),
                currentUser.getFirstname(), currentUser.getLastname());
            res.setUser(userLogin);

        var accessToken = securityUtil.generateAccessToken(email, res.getUser());
        var newRefreshToken = securityUtil.generateRefreshToken(email, res.getUser());

        res.setAccessToken(accessToken);
        userService.updateUserToken(newRefreshToken, email);

        return res;
    }

    public void logout() {
        var email = SecurityUtil.getCurrentUserLogin()
                .orElseThrow(() -> new RuntimeException("Invalid Refresh Token"));

        userService.updateUserToken(null, email);
    }

    public LoginResponse.UserLogin getAccount() {
        var email = SecurityUtil.getCurrentUserLogin()
                .orElseThrow(() -> new RuntimeException("Invalid Refresh Token"));

        var currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return new LoginResponse.UserLogin(
                currentUser.getId(), currentUser.getUsername(), currentUser.getEmail(),
                currentUser.getFirstname(), currentUser.getLastname());
    }

    public ResponseCookie createDeleteCookie() {
        return ResponseCookie.from("refreshToken", null)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("Strict")
                .maxAge(0)
                .build();
    }
}
