package com.jobhunt.service.impl;

import com.jobhunt.exception.BadRequestException;
import com.jobhunt.mapper.UserMapper;
import com.jobhunt.model.entity.User;
import com.jobhunt.model.request.ChangePasswordRequest;
import com.jobhunt.model.request.LoginRequest;
import com.jobhunt.model.request.SignUpRequest;
import com.jobhunt.model.response.AuthResponse;
import com.jobhunt.repository.UserRepository;
import com.jobhunt.service.AuthService;
import com.jobhunt.util.SecurityUtil;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

  private final UserRepository userRepository;
  private final SecurityUtil securityUtil;
  private final UserMapper userMapper;

  @Value("${keycloak.auth-server-url}")
  private String authServerUrl;

  @Value("${keycloak.realm}")
  private String realm;

  @Value("${keycloak.resource}")
  private String clientId;

  @Value("${keycloak.credentials.secret}")
  private String clientSecret;

  @Override
  @Transactional
  public AuthResponse login(LoginRequest request) {
    try {
      Keycloak keycloak = KeycloakBuilder.builder()
          .serverUrl(authServerUrl)
          .realm(realm)
          .clientId(clientId)
          .clientSecret(clientSecret)
          .username(request.getEmail())
          .password(request.getPassword())
          .build();

      // This will throw an exception if authentication fails
      String accessToken = keycloak.tokenManager().getAccessToken().getToken();

      User user = userRepository.findByEmail(request.getEmail())
          .orElseThrow(() -> new BadRequestException("User not found"));

      String refreshToken = securityUtil.generateRefreshToken(request.getEmail(), null); // TODO: Add user login info

      return AuthResponse.builder()
          .accessToken(accessToken)
          .refreshToken(refreshToken)
          .tokenType("Bearer")
          .expiresIn(3600L) // 1 hour
          .user(userMapper.toResponse(user))
          .build();
    } catch (Exception e) {
      throw new BadRequestException("Invalid credentials");
    }
  }

  @Override
  @Transactional
  public AuthResponse signup(SignUpRequest request) {
    if (!request.getPassword().equals(request.getConfirmPassword())) {
      throw new BadRequestException("Password and confirm password do not match");
    }

    if (userRepository.findByEmail(request.getEmail()).isPresent()) {
      throw new BadRequestException("Email already exists");
    }

    if (userRepository.findByUsername(request.getUsername()).isPresent()) {
      throw new BadRequestException("Username already exists");
    }

    Keycloak keycloak = getAdminKeycloak();
    RealmResource realmResource = keycloak.realm(realm);
    UsersResource usersResource = realmResource.users();

    // Create user in Keycloak
    UserRepresentation user = new UserRepresentation();
    user.setEnabled(true);
    user.setUsername(request.getUsername());
    user.setEmail(request.getEmail());
    user.setFirstName(request.getFirstName());
    user.setLastName(request.getLastName());
    user.setEmailVerified(false);

    Response response = usersResource.create(user);

    if (response.getStatus() != 201) {
      throw new BadRequestException("Failed to create user in Keycloak");
    }

    // Set password
    String userId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");

    CredentialRepresentation passwordCred = new CredentialRepresentation();
    passwordCred.setTemporary(false);
    passwordCred.setType(CredentialRepresentation.PASSWORD);
    passwordCred.setValue(request.getPassword());

    usersResource.get(userId).resetPassword(passwordCred);

    // Create user in our database
    User newUser = new User();
    newUser.setUsername(request.getUsername());
    newUser.setEmail(request.getEmail());
    newUser.setFirstName(request.getFirstName());
    newUser.setLastName(request.getLastName());
    newUser.setPhoneNumber(request.getPhoneNumber());
    newUser.setKeycloakId(userId);
    newUser = userRepository.save(newUser);

    // Log the user in
    return login(new LoginRequest(request.getEmail(), request.getPassword()));
  }

  @Override
  @Transactional
  public void logout(String refreshToken) {
    User user = userRepository.findByRefreshTokenAndEmail(refreshToken, SecurityUtil.getCurrentUserLogin()
        .orElseThrow(() -> new BadRequestException("User not found")))
        .orElseThrow(() -> new BadRequestException("Invalid refresh token"));

    user.setRefreshToken(null);
    userRepository.save(user);

    // Logout from Keycloak
    try {
      Keycloak keycloak = getAdminKeycloak();
      keycloak.realm(realm).users().get(user.getKeycloakId()).logout();
    } catch (Exception e) {
      // Log the error but don't throw it since we've already invalidated the refresh
      // token
      e.printStackTrace();
    }
  }

  @Override
  @Transactional
  public AuthResponse refreshToken(String refreshToken) {
    try {
      securityUtil.checkValidRefreshToken(refreshToken);

      User user = userRepository.findByRefreshTokenAndEmail(refreshToken, SecurityUtil.getCurrentUserLogin()
          .orElseThrow(() -> new BadRequestException("User not found")))
          .orElseThrow(() -> new BadRequestException("Invalid refresh token"));

      // Get new access token from Keycloak
      Keycloak keycloak = getAdminKeycloak();
      String newAccessToken = keycloak.tokenManager().getAccessToken().getToken();

      // Generate new refresh token
      String newRefreshToken = securityUtil.generateRefreshToken(user.getEmail(), null); // TODO: Add user login info

      user.setRefreshToken(newRefreshToken);
      userRepository.save(user);

      return AuthResponse.builder()
          .accessToken(newAccessToken)
          .refreshToken(newRefreshToken)
          .tokenType("Bearer")
          .expiresIn(3600L)
          .user(userMapper.toResponse(user))
          .build();
    } catch (Exception e) {
      throw new BadRequestException("Invalid refresh token");
    }
  }

  @Override
  @Transactional
  public void changePassword(Long userId, ChangePasswordRequest request) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new BadRequestException("User not found"));

    try {
      Keycloak keycloak = getAdminKeycloak();

      CredentialRepresentation passwordCred = new CredentialRepresentation();
      passwordCred.setTemporary(false);
      passwordCred.setType(CredentialRepresentation.PASSWORD);
      passwordCred.setValue(request.getNewPassword());

      keycloak.realm(realm).users().get(user.getKeycloakId()).resetPassword(passwordCred);
    } catch (Exception e) {
      throw new BadRequestException("Failed to change password");
    }
  }

  @Override
  @Transactional
  public void resetPassword(String email) {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new BadRequestException("User not found"));

    try {
      Keycloak keycloak = getAdminKeycloak();
      keycloak.realm(realm).users().get(user.getKeycloakId()).executeActionsEmail(List.of("UPDATE_PASSWORD"));
    } catch (Exception e) {
      throw new BadRequestException("Failed to send reset password email");
    }
  }

  @Override
  @Transactional
  public void confirmResetPassword(String token, String newPassword) {
    // This should be handled by Keycloak's reset password flow
    throw new UnsupportedOperationException("Password reset confirmation is handled by Keycloak");
  }

  private Keycloak getAdminKeycloak() {
    return KeycloakBuilder.builder()
        .serverUrl(authServerUrl)
        .realm("master")
        .clientId("admin-cli")
        .username("admin")
        .password("admin")
        .build();
  }
}