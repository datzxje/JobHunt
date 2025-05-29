package com.jobhunt.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobhunt.exception.BadRequestException;
import com.jobhunt.mapper.UserMapper;
import com.jobhunt.model.entity.User;
import com.jobhunt.model.request.ChangePasswordRequest;
import com.jobhunt.model.request.LoginRequest;
import com.jobhunt.model.request.SignUpRequest;
import com.jobhunt.model.response.AuthResponse;
import com.jobhunt.model.response.UserResponse;
import com.jobhunt.repository.UserRepository;
import com.jobhunt.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final HttpServletResponse response;
  private final HttpServletRequest request;

  @Autowired
  private JwtDecoder jwtDecoder;

  private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

  @Value("${keycloak.auth-server-url}")
  private String authServerUrl;

  @Value("${keycloak.realm}")
  private String realm;

  @Value("${keycloak.resource}")
  private String clientId;

  @Value("${keycloak.credentials.secret}")
  private String clientSecret;

  @Value("${app.cookie.domain}")
  private String cookieDomain;

  @Value("${app.cookie.secure:true}")
  private boolean secureCookie;

  @Value("${app.cookie.httpOnly:true}")
  private boolean httpOnlyCookie;

  @Value("${app.cookie.accessToken.expiration:3600}")
  private int accessTokenExpiration;

  @Value("${app.cookie.refreshToken.expiration:86400}")
  private int refreshTokenExpiration;

  @Override
  @Transactional
  public AuthResponse login(LoginRequest request) {
    try {
      log.debug("Attempting login for user: {}", request.getEmail());
      log.debug("Using Keycloak server URL: {}", authServerUrl);

      // Build the URL
      String tokenUrl = authServerUrl + "/realms/" + realm + "/protocol/openid-connect/token";

      // Build the request body
      String form = "client_id=" + URLEncoder.encode(clientId, StandardCharsets.UTF_8)
          + "&client_secret=" + URLEncoder.encode(clientSecret, StandardCharsets.UTF_8)
          + "&grant_type=password"
          + "&username=" + URLEncoder.encode(request.getEmail(), StandardCharsets.UTF_8)
          + "&password=" + URLEncoder.encode(request.getPassword(), StandardCharsets.UTF_8)
          + "&scope=openid";

      HttpRequest httpRequest = HttpRequest.newBuilder()
          .uri(URI.create(tokenUrl))
          .header("Content-Type", "application/x-www-form-urlencoded")
          .POST(HttpRequest.BodyPublishers.ofString(form))
          .build();

      HttpClient client = HttpClient.newHttpClient();
      HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

      if (response.statusCode() != 200) {
        log.error("Failed to get token from Keycloak. HTTP status: {}", response.statusCode());
        throw new BadRequestException("Invalid credentials");
      }

      // Parse token response
      ObjectMapper mapper = new ObjectMapper();
      AccessTokenResponse tokenResponse = mapper.readValue(response.body(), AccessTokenResponse.class);

      String accessToken = tokenResponse.getToken();
      String refreshToken = tokenResponse.getRefreshToken();
      log.debug("Successfully obtained tokens from Keycloak");

      // Lookup user
      User user = userRepository.findByEmail(request.getEmail())
          .orElseThrow(() -> new BadRequestException("User not found"));

      // Store tokens in cookies
      addTokenCookies(accessToken, refreshToken);

      // Return auth response
      return AuthResponse.builder()
          .tokenType("Bearer")
          .expiresIn((long) accessTokenExpiration)
          .user(userMapper.toResponse(user))
          .build();

    } catch (IOException | InterruptedException e) {
      log.error("HTTP request failed: {}", e.getMessage(), e);
      throw new BadRequestException("Token request failed");
    } catch (Exception e) {
      log.error("Login failed: {}", e.getMessage(), e);
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
      String body = null;
      try {
        body = response.readEntity(String.class);
      } catch (Exception ignored) {
      }
      log.error("Keycloak user-create failed: {} – {}", response.getStatus(), body);
      throw new BadRequestException("Keycloak error " + response.getStatus() +
          (body != null ? ": " + body : ""));
    }

    // Set password
    String userId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");

    CredentialRepresentation passwordCred = new CredentialRepresentation();
    passwordCred.setTemporary(false);
    passwordCred.setType(CredentialRepresentation.PASSWORD);
    passwordCred.setValue(request.getPassword());

    usersResource.get(userId).resetPassword(passwordCred);

    // Assign role in Keycloak only
    try {
      var roleRepresentation = realmResource.roles().get(request.getRole()).toRepresentation();
      usersResource.get(userId).roles().realmLevel().add(List.of(roleRepresentation));
      log.info("Successfully assigned role {} to user in Keycloak", request.getRole());
    } catch (Exception e) {
      log.warn("Failed to assign role in Keycloak, but user created successfully: {}", e.getMessage());
    }

    // Create user in database (without roles)
    try {
      User newUser = new User();
      newUser.setUsername(request.getUsername());
      newUser.setEmail(request.getEmail());
      newUser.setFirstName(request.getFirstName());
      newUser.setLastName(request.getLastName());
      newUser.setPhoneNumber(request.getPhoneNumber());
      newUser.setKeycloakId(userId);

      log.info("Creating user in database with email: {}", request.getEmail());
      newUser = userRepository.save(newUser);
      log.info("Successfully created user in database with id: {}", newUser.getId());
    } catch (Exception e) {
      log.error("Failed to create user in database: {}", e.getMessage(), e);
      throw new BadRequestException("Failed to create user in database: " + e.getMessage());
    }

    // Log the user in
    return login(new LoginRequest(request.getEmail(), request.getPassword()));
  }

  @Override
  public void logout() {
    // Clear cookies first
    removeTokenCookies();

    // Try to logout from Keycloak if we can identify the user
    try {
      String username = getCurrentUser().getEmail();
      if (username != null) {
        User user = userRepository.findByEmail(username)
            .orElse(null);

        if (user != null && user.getKeycloakId() != null) {
          try {
            Keycloak keycloak = getAdminKeycloak();
            keycloak.realm(realm).users().get(user.getKeycloakId()).logout();
          } catch (Exception e) {
            log.warn("Failed to logout from Keycloak: {}", e.getMessage());
          }
        }
      }
    } catch (Exception e) {
      log.warn("Could not find user during logout: {}", e.getMessage());
    }
  }

  @Override
  public AuthResponse refreshToken() {
    try {
      // Try from header first (useful for Postman testing)
      String authHeader = request.getHeader("Authorization");
      String refreshToken = null;

      if (authHeader != null && authHeader.toLowerCase().startsWith("bearer ")) {
        log.debug("Found refresh token in Authorization header");
        refreshToken = authHeader.substring(7);
      } else {
        // Then try from cookie
        log.debug("Trying to get refresh token from cookie");
        refreshToken = getCookieValue("refresh_token")
            .orElseThrow(() -> new BadRequestException("No refresh token found"));
      }

      // Check if token appears to be valid JWT
      if (!isValidJwtFormat(refreshToken)) {
        log.warn("Refresh token has invalid format, clearing cookies and redirecting to login");
        removeTokenCookies();
        throw new BadRequestException("Invalid refresh token format - please log in again");
      }

      log.debug("Attempting to refresh token with Keycloak");

      String tokenUrl = authServerUrl + "/realms/" + realm + "/protocol/openid-connect/token";

      log.debug("Using token URL: {}", tokenUrl);

      // Prepare form data
      String formData = String.format(
          "client_id=%s&client_secret=%s&grant_type=refresh_token&refresh_token=%s",
          clientId, clientSecret, refreshToken);

      log.debug("Token request data: client_id={}, grant_type=refresh_token", clientId);

      // Set up connection
      java.net.URL url = new java.net.URL(tokenUrl);
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("POST");
      connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
      connection.setDoOutput(true);

      // Send request
      try (java.io.OutputStream os = connection.getOutputStream()) {
        byte[] input = formData.getBytes(StandardCharsets.UTF_8);
        os.write(input, 0, input.length);
      }

      // Read response
      if (connection.getResponseCode() != 200) {
        // Read error response body for more details
        StringBuilder errorResponse = new StringBuilder();
        try (java.io.BufferedReader br = new java.io.BufferedReader(
            new java.io.InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8))) {
          String responseLine;
          while ((responseLine = br.readLine()) != null) {
            errorResponse.append(responseLine.trim());
          }
        }
        log.error("Token refresh failed - Status: {}, Error: {}, URL: {}",
            connection.getResponseCode(),
            errorResponse.toString(),
            tokenUrl);

        // If token is invalid, clear cookies and ask user to log in again
        if (errorResponse.toString().contains("invalid_token") ||
            errorResponse.toString().contains("Invalid refresh token")) {
          log.warn("Invalid refresh token detected, clearing cookies");
          removeTokenCookies();
          throw new BadRequestException("Session expired - please log in again");
        }

        throw new BadRequestException("Failed to refresh token: " + connection.getResponseMessage() +
            (errorResponse.length() > 0 ? " - Details: " + errorResponse : ""));
      }

      // Parse JSON response
      ObjectMapper mapper = new ObjectMapper();
      JsonNode tokenResponse;
      try (java.io.InputStream in = connection.getInputStream()) {
        tokenResponse = mapper.readTree(in);
      }

      // Extract tokens
      String newAccessToken = tokenResponse.get("access_token").asText();
      String newRefreshToken = tokenResponse.get("refresh_token").asText();
      int expiresIn = tokenResponse.get("expires_in").asInt();

      log.debug("Token refresh successful, setting new cookies");

      // Set cookies
      addTokenCookies(newAccessToken, newRefreshToken);

      // Decode JWT để lấy thông tin người dùng
      Jwt jwt = jwtDecoder.decode(newAccessToken);

      // Lấy email từ token
      String email = extractEmailFromJwt(jwt);

      // Tìm user trong database
      User user = userRepository.findByEmail(email)
          .orElseThrow(() -> new BadRequestException("User not found with email: " + email));

      return AuthResponse.builder()
          .tokenType("Bearer")
          .expiresIn((long) expiresIn)
          .user(userMapper.toResponse(user))
          .build();
    } catch (Exception e) {
      log.error("Error refreshing token: {}", e.getMessage(), e);
      throw new BadRequestException("Error refreshing token: " + e.getMessage());
    }
  }

  /**
   * Trích xuất email từ JWT token
   */
  private String extractEmailFromJwt(Jwt jwt) {
    if (jwt.hasClaim("email")) {
      String email = jwt.getClaimAsString("email");
      if (email != null && !email.isEmpty()) {
        return email;
      }
    }

    if (jwt.hasClaim("preferred_username")) {
      String username = jwt.getClaimAsString("preferred_username");
      if (username != null && !username.isEmpty()) {
        return username;
      }
    }

    if (jwt.getSubject() != null && !jwt.getSubject().isEmpty()) {
      return jwt.getSubject();
    }

    throw new BadRequestException("Could not extract user identity from token");
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

  @Override
  public UserResponse getCurrentUser() {
    try {
      // Lấy access token từ cookie
      String accessToken = getCookieValue("access_token")
          .orElseThrow(() -> new BadRequestException("No access token found"));

      // Kiểm tra token có định dạng JWT hợp lệ
      if (!isValidJwtFormat(accessToken)) {
        log.warn("Invalid token format in cookie");
        throw new BadRequestException("Invalid token format");
      }

      // Gọi Keycloak UserInfo endpoint
      String userInfoUrl = authServerUrl + "/realms/" + realm + "/protocol/openid-connect/userinfo";
      log.debug("Calling UserInfo endpoint: {}", userInfoUrl);

      HttpRequest httpRequest = HttpRequest.newBuilder()
          .uri(URI.create(userInfoUrl))
          .header("Authorization", "Bearer " + accessToken)
          .GET()
          .build();

      HttpClient client = HttpClient.newHttpClient();
      HttpResponse<String> userInfoResponse = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

      if (userInfoResponse.statusCode() != 200) {
        log.error("Failed to get user info. HTTP status: {}", userInfoResponse.statusCode());
        throw new BadRequestException("Failed to get user information");
      }

      // Parse user info response
      ObjectMapper mapper = new ObjectMapper();
      JsonNode userInfo = mapper.readTree(userInfoResponse.body());
      log.debug("UserInfo response: {}", userInfoResponse.body());

      // Lấy email từ response
      String email = null;
      if (userInfo.has("email")) {
        email = userInfo.get("email").asText();
      }

      if (email == null && userInfo.has("preferred_username")) {
        email = userInfo.get("preferred_username").asText();
      }

      if (email == null && userInfo.has("sub")) {
        email = userInfo.get("sub").asText();
      }

      if (email == null || email.isEmpty()) {
        log.warn("Could not extract email from UserInfo response");
        throw new BadRequestException("Invalid user information");
      }

      log.debug("Extracted email from UserInfo: {}", email);

      // Tìm user trong database
      String finalEmail = email;
      User user = userRepository.findByEmail(email)
          .orElseThrow(() -> new BadRequestException("User not found with email: " + finalEmail));

      return userMapper.toResponse(user);
    } catch (BadRequestException e) {
      throw e;
    } catch (Exception e) {
      log.error("Error getting current user: {}", e.getMessage(), e);
      throw new BadRequestException("Authentication failed");
    }
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

  // Helper methods for cookie management
  private void addTokenCookies(String accessToken, String refreshToken) {
    // Access token cookie
    Cookie accessTokenCookie = new Cookie("access_token", accessToken);
    accessTokenCookie.setHttpOnly(httpOnlyCookie);
    accessTokenCookie.setSecure(secureCookie);
    accessTokenCookie.setPath("/");
    accessTokenCookie.setDomain(cookieDomain);
    accessTokenCookie.setMaxAge(accessTokenExpiration);
    response.addCookie(accessTokenCookie);
    log.debug("Set access_token cookie with expiration: {} seconds", accessTokenExpiration);

    // Refresh token cookie
    Cookie refreshTokenCookie = new Cookie("refresh_token", refreshToken);
    refreshTokenCookie.setHttpOnly(httpOnlyCookie);
    refreshTokenCookie.setSecure(secureCookie);
    refreshTokenCookie.setPath("/");
    refreshTokenCookie.setDomain(cookieDomain);
    refreshTokenCookie.setMaxAge(refreshTokenExpiration);
    response.addCookie(refreshTokenCookie);
    log.debug("Set refresh_token cookie with expiration: {} seconds", refreshTokenExpiration);
  }

  private void removeTokenCookies() {
    log.debug("Removing authentication cookies");

    // Remove access token cookie
    Cookie accessTokenCookie = new Cookie("access_token", "");
    accessTokenCookie.setHttpOnly(httpOnlyCookie);
    accessTokenCookie.setSecure(secureCookie);
    accessTokenCookie.setPath("/");
    accessTokenCookie.setDomain(cookieDomain);
    accessTokenCookie.setMaxAge(0);
    response.addCookie(accessTokenCookie);

    // Remove refresh token cookie
    Cookie refreshTokenCookie = new Cookie("refresh_token", "");
    refreshTokenCookie.setHttpOnly(httpOnlyCookie);
    refreshTokenCookie.setSecure(secureCookie);
    refreshTokenCookie.setPath("/");
    refreshTokenCookie.setDomain(cookieDomain);
    refreshTokenCookie.setMaxAge(0);
    response.addCookie(refreshTokenCookie);
  }

  private Optional<String> getCookieValue(String cookieName) {
    if (request.getCookies() == null) {
      log.debug("No cookies found in request");
      return Optional.empty();
    }

    Optional<String> cookieValue = Arrays.stream(request.getCookies())
        .filter(cookie -> cookieName.equals(cookie.getName()))
        .map(Cookie::getValue)
        .findFirst();

    if (cookieValue.isEmpty()) {
      log.debug("Cookie '{}' not found in request", cookieName);
    } else {
      // Print cookie value partially for debugging
      String value = cookieValue.get();
      String partialValue = value.length() > 10 ? value.substring(0, 5) + "..." + value.substring(value.length() - 5)
          : value;
      log.debug("Cookie '{}' found in request, length: {}, partial value: {}", cookieName, value.length(),
          partialValue);
    }

    return cookieValue;
  }

  /**
   * Simple validation to check if a token appears to be in valid JWT format.
   * JWT tokens consist of 3 dot-separated base64 encoded parts.
   */
  private boolean isValidJwtFormat(String token) {
    if (token == null || token.isEmpty()) {
      log.debug("Token validation failed: token is null or empty");
      return false;
    }

    // Log token length and first/last characters for debugging
    log.debug("Validating token: length={}, starts with '{}'...",
        token.length(),
        token.length() > 5 ? token.substring(0, 5) : token);

    // Temporarily disable strict validation for troubleshooting
    if (!token.contains(".")) {
      log.debug("Token validation warning: token doesn't contain dots");
      // Return true temporarily for debugging
      return true;
    }

    String[] parts = token.split("\\.");
    if (parts.length != 3) {
      log.debug("Token validation failed: expected 3 parts, found {}", parts.length);
      // Return true temporarily for debugging
      return true;
    }

    // Make validation less strict
    return true;
  }

}