package com.jobhunt.service;

import com.jobhunt.model.request.UpdateAvatarRequest;
import com.jobhunt.model.request.UserRequest;
import com.jobhunt.model.response.UserResponse;
import org.springframework.data.domain.Page;

public interface UserService {

    UserResponse getUserById(Long userID);

    Page<UserResponse> getAllUsers(int page, int size);

    Page<UserResponse> searchUsers(String name, String email, String predicateType,
            int pageNumber, int pageSize, String sortBy, String sortDir);

    UserResponse updateUser(Long userID, UserRequest userRequest);

    UserResponse deleteUser(Long userID);

    UserResponse getCurrentUser();

    UserResponse updateAvatar(Long userId, UpdateAvatarRequest request);

}
