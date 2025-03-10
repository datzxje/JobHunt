package com.jobhunt.service;

import com.jobhunt.model.request.ChangePasswordRequest;
import com.jobhunt.model.request.SignUpRequest;
import com.jobhunt.model.request.UserRequest;
import com.jobhunt.model.request.VerificationRequest;
import com.jobhunt.model.response.SignUpResponse;
import com.jobhunt.model.response.UserResponse;
import org.springframework.data.domain.Page;

public interface UserService {

    SignUpResponse sendVerificationCode(SignUpRequest signUpRequest);

    String resendVerificationCode(String email);

    UserResponse verifyAndCreateUser(VerificationRequest verificationRequest);

    UserResponse changePassword(ChangePasswordRequest request);

    UserResponse getUserById(Long userID);

    Page<UserResponse> getAllUsers(int page);

    Page<UserResponse> searchUsers(String name, String email, String predicateType,
                                   int pageNumber, int pageSize, String sortBy, String sortDir);

    UserResponse updateUser(Long userID, UserRequest userRequest);

    UserResponse deleteUser(Long userID);

    void updateUserToken(String token, String email);

}
