package com.jobhunt.service;

import com.jobhunt.model.request.LoginRequest;
import com.jobhunt.model.response.LoginResponse;
import org.springframework.http.ResponseCookie;

public interface AuthService {
    LoginResponse login(LoginRequest loginRequest);

    ResponseCookie createRefreshCookie(String refreshToken);

    LoginResponse refresh(String refreshToken);

    void logout();

    ResponseCookie createDeleteCookie();

    LoginResponse.UserLogin getAccount();
}
