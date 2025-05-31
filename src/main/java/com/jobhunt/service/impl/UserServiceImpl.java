package com.jobhunt.service.impl;

import com.jobhunt.mapper.UserMapper;
import com.jobhunt.model.entity.User;
import com.jobhunt.model.request.UpdateAvatarRequest;
import com.jobhunt.model.request.UserRequest;
import com.jobhunt.model.response.UserResponse;
import com.jobhunt.repository.UserRepository;
import com.jobhunt.service.UserService;
import com.jobhunt.specification.GenericSpecification;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public UserResponse getUserById(Long userID) {
        var optionalUser = userRepository.findById(userID)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return userMapper.toResponse(optionalUser);
    }

    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.findAll(pageable)
                .map(userMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<UserResponse> searchUsers(String name, String email, String predicateType,
            int pageNumber, int pageSize, String sortBy, String sortDir) {
        GenericSpecification<User> builder = new GenericSpecification<>();
        if (name != null) {
            builder.with("lastname", "=", name, predicateType);
        }
        if (email != null) {
            builder.with("email", "=", email, predicateType);
        }

        Pageable pageable;
        if (sortDir == null || sortBy == null) {
            pageable = PageRequest.of(pageNumber, pageSize);
        } else {
            Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                    ? Sort.by(sortBy).ascending()
                    : Sort.by(sortBy).descending();
            pageable = PageRequest.of(pageNumber, pageSize, sort);
        }
        return userRepository.findAll(builder.build(), pageable)
                .map(userMapper::toResponse);
    }

    @Transactional
    public UserResponse updateUser(Long userID, UserRequest userRequest) {
        var user = userRepository.findById(userID)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        userMapper.updateUserFromDto(userRequest, user);

        userRepository.save(user);
        return userMapper.toResponse(user);
    }

    @Transactional
    public UserResponse deleteUser(Long userID) {
        var optionalUser = userRepository.findById(userID)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        userRepository.delete(optionalUser);
        return userMapper.toResponse(optionalUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getCurrentUser() {
        String currentUserId = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findById(Long.parseLong(currentUserId))
                .map(userMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Current user not found"));
    }

    @Override
    @Transactional
    public UserResponse updateAvatar(Long userId, UpdateAvatarRequest request) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        user.setProfilePictureUrl(request.getAvatarUrl());
        return userMapper.toResponse(userRepository.save(user));
    }
}