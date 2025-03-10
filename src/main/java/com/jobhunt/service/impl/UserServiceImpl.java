package com.jobhunt.service.impl;

import com.jobhunt.enums.StatusEnum;
import com.jobhunt.mapper.UserMapper;
import com.jobhunt.model.entity.User;
import com.jobhunt.model.entity.VerificationCode;
import com.jobhunt.model.request.ChangePasswordRequest;
import com.jobhunt.model.request.SignUpRequest;
import com.jobhunt.model.request.UserRequest;
import com.jobhunt.model.request.VerificationRequest;
import com.jobhunt.model.response.SignUpResponse;
import com.jobhunt.model.response.UserResponse;
import com.jobhunt.repository.UserRepository;
import com.jobhunt.repository.VerificationCodeRepository;
import com.jobhunt.service.UserService;
import com.jobhunt.specification.GenericSpecification;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper = UserMapper.INSTANCE;
    private final EmailServiceImpl emailService;
    private final VerificationCodeRepository verificationCodeRepository;

    @Transactional
    public SignUpResponse sendVerificationCode(SignUpRequest signUpRequest) {
        if (userRepository.findByEmail(signUpRequest.getEmail()).isPresent()) {
            throw new EntityExistsException("User already exists");
        }

        if (userRepository.findByUsername(signUpRequest.getUsername()).isPresent()) {
            throw new EntityExistsException("Username already exists");
        }

        var verificationCode = verificationCodeRepository.findByEmail(signUpRequest.getEmail());
        verificationCode.ifPresent(verificationCodeRepository::delete);

        try {
            var newCodeEntity = createVerificationCode(signUpRequest.getEmail());
            emailService.sendVerificationEmail(signUpRequest.getEmail(), newCodeEntity.getCode());

            var signUpResponse = userMapper.toResponse(signUpRequest);
            signUpResponse.setStatus(StatusEnum.INACTIVE.toString());

            return signUpResponse;
        } catch (Exception e) {
            throw new RuntimeException("Error sending verification code", e);
        }
    }

    @Transactional
    public String resendVerificationCode(String email) {
        var verificationCode = verificationCodeRepository.findByEmail(email);

        verificationCode.ifPresent(verificationCodeRepository::delete);

        try {
            var newCodeEntity = createVerificationCode(email);
            emailService.sendVerificationEmail(email, newCodeEntity.getCode());

            return newCodeEntity.getCode();
        } catch (Exception e) {
            throw new RuntimeException("Error resending verification code", e);
        }
    }

    private VerificationCode createVerificationCode(String email) {
        var newVerificationCode = String.valueOf(new Random().nextInt(900000) + 100000);
        var newCodeEntity = new VerificationCode();
        newCodeEntity.setEmail(email);
        newCodeEntity.setCode(newVerificationCode);
        newCodeEntity.setExpireAt(LocalDateTime.now().plusMinutes(1));
        return verificationCodeRepository.save(newCodeEntity);
    }

    @Transactional
    public UserResponse verifyAndCreateUser(VerificationRequest verificationRequest) {
        var codeEntity = verificationCodeRepository.findByEmail(verificationRequest.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("No verification request found for this email"));

        // Check if the code matches and is not expired
        if (!codeEntity.getCode().equals(verificationRequest.getCode())) {
            throw new IllegalArgumentException("Invalid verification code");
        }
        if (codeEntity.getExpireAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Verification code expired");
        }

        // Create user
        var signUpRequest = verificationRequest.getSignUpRequest();
        var hashedPassword = passwordEncoder.encode(signUpRequest.getPassword());
        var newUser = userMapper.toEntity(signUpRequest);
        newUser.setPassword(hashedPassword);
        newUser.setStatus(StatusEnum.ACTIVE);

        userRepository.save(newUser);

        // Remove verification entry
        verificationCodeRepository.delete(codeEntity);

        return userMapper.toResponse(newUser);
    }

    public UserResponse changePassword(ChangePasswordRequest request) {
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        // Update to the new password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        return userMapper.toResponse(user);
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(Long userID) {
        var optionalUser = userRepository.findById(userID)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return userMapper.toResponse(optionalUser);
    }

    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(int page) {
        Pageable pageable = PageRequest.of(page, 6);
        return userRepository.findAll(pageable)
                .map(userMapper::toResponse); // Use map to convert entities to responses
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

    public UserResponse deleteUser(Long userID) {
        var optionalUser = userRepository.findById(userID)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        userRepository.delete(optionalUser);
        return userMapper.toResponse(optionalUser);
    }

    @Transactional
    public void updateUserToken(String token, String email) {
        var currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        currentUser.setRefreshToken(token);
        userRepository.save(currentUser);
    }
}
