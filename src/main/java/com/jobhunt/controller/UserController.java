package com.jobhunt.controller;

import com.jobhunt.model.request.ChangePasswordRequest;
import com.jobhunt.model.request.SignUpRequest;
import com.jobhunt.model.request.UserRequest;
import com.jobhunt.model.request.VerificationRequest;
import com.jobhunt.model.response.SignUpResponse;
import com.jobhunt.model.response.UserResponse;
import com.jobhunt.payload.Response;
import com.jobhunt.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<Response<SignUpResponse>> sendVerificationCode(@Valid @RequestBody SignUpRequest signUpRequest) {
        return ResponseEntity.ok(Response.ofSucceeded(userService.sendVerificationCode(signUpRequest)));
    }

    @PostMapping("/verify")
    public ResponseEntity<Response<UserResponse>> verifyAndCreateUser(
            @Valid @RequestBody VerificationRequest verificationRequest) {
        return ResponseEntity.ok(Response.ofSucceeded(userService.verifyAndCreateUser(verificationRequest)));
    }

    @PostMapping("/resend-code")
    public ResponseEntity<Response<?>> resendVerificationCode(
            @RequestParam String email) {
        return ResponseEntity.ok(Response.ofSucceeded(userService.resendVerificationCode(email)));
    }

    @PostMapping("/change-password")
    public ResponseEntity<Response<UserResponse>> changePassword(@RequestBody @Valid ChangePasswordRequest request) {
        return ResponseEntity.ok(Response.ofSucceeded(userService.changePassword(request)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(Response.ofSucceeded(userService.getUserById(id)));
    }

    @GetMapping("")
    public ResponseEntity<?> getAllUsers(@RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.ok(Response.ofSucceeded(userService.getAllUsers(page)));
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchUsers(@RequestParam(required = false) String name,
                                         @RequestParam(required = false) String email,
                                         @RequestParam(required = false, defaultValue = "AND") String predicateType,
                                         @RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "6") int pageSize,
                                         @RequestParam(required = false) String sortBy,
                                         @RequestParam(required = false) String sortDir) {
        return ResponseEntity.ok(Response.
                ofSucceeded(userService.searchUsers(name, email, predicateType, page, pageSize, sortBy, sortDir)));
    }

    @PutMapping("/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable Long userId, @RequestBody UserRequest userRequest) {
        return ResponseEntity.ok(Response.ofSucceeded(userService.updateUser(userId, userRequest)));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
        return ResponseEntity.ok(Response.ofSucceeded(userService.deleteUser(userId)));
    }
}
