package com.jobhunt.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobhunt.exception.BadRequestException;
import com.jobhunt.mapper.UserMapper;
import com.jobhunt.model.entity.User;
import com.jobhunt.model.response.AuthResponse;
import com.jobhunt.model.response.UserResponse;
import com.jobhunt.repository.UserRepository;
import com.jobhunt.service.OAuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OAuthServiceImpl implements OAuthService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final HttpServletResponse response;

  @Autowired
  private JwtDecoder jwtDecoder;

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

  @Value("${app.oauth.google.client-id}")
  private String googleClientId;

  @Value("${app.oauth.google.client-secret}")
  private String googleClientSecret;

  @Override
  public String getGoogleAuthUrl() {
    // Use backend redirect URI instead of Keycloak
    String redirectUri = "http://localhost:8080/profile/api/v1/auth/oauth/callback";
    String state = UUID.randomUUID().toString();

    return "https://accounts.google.com/o/oauth2/v2/auth?" +
        "client_id=" + URLEncoder.encode(googleClientId, StandardCharsets.UTF_8) +
        "&redirect_uri=" + URLEncoder.encode(redirectUri, StandardCharsets.UTF_8) +
        "&response_type=code" +
        "&scope=" + URLEncoder.encode("openid email profile", StandardCharsets.UTF_8) +
        "&state=" + state;
  }

  @Override
  @Transactional
  public AuthResponse handleOAuthCallback(String code) {
    try {
      log.info("Processing OAuth callback with code: {}", code.substring(0, Math.min(10, code.length())) + "...");

      // Option 1: Try Keycloak flow first
      try {
        return handleKeycloakOAuthCallback(code);
      } catch (Exception e) {
        log.warn("Keycloak OAuth flow failed, trying direct Google OAuth: {}", e.getMessage());
        return handleDirectGoogleOAuthCallback(code);
      }

    } catch (IOException | InterruptedException e) {
      log.error("HTTP request failed during OAuth callback: {}", e.getMessage(), e);
      throw new BadRequestException("OAuth callback processing failed");
    } catch (Exception e) {
      log.error("OAuth callback failed: {}", e.getMessage(), e);
      throw new BadRequestException("OAuth authentication failed");
    }
  }

  private AuthResponse handleKeycloakOAuthCallback(String code) throws IOException, InterruptedException {
    // 1. Exchange code for tokens using Keycloak
    String tokenUrl = authServerUrl + "/realms/" + realm + "/protocol/openid-connect/token";

    String form = "client_id=" + URLEncoder.encode(clientId, StandardCharsets.UTF_8)
        + "&client_secret=" + URLEncoder.encode(clientSecret, StandardCharsets.UTF_8)
        + "&grant_type=authorization_code"
        + "&code=" + URLEncoder.encode(code, StandardCharsets.UTF_8)
        + "&redirect_uri="
        + URLEncoder.encode("http://localhost:8080/profile/api/v1/auth/oauth/callback", StandardCharsets.UTF_8);

    HttpRequest httpRequest = HttpRequest.newBuilder()
        .uri(URI.create(tokenUrl))
        .header("Content-Type", "application/x-www-form-urlencoded")
        .POST(HttpRequest.BodyPublishers.ofString(form))
        .build();

    HttpClient client = HttpClient.newHttpClient();
    HttpResponse<String> tokenResponse = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

    if (tokenResponse.statusCode() != 200) {
      log.error("Failed to exchange code for tokens. HTTP status: {}", tokenResponse.statusCode());
      log.error("Response body: {}", tokenResponse.body());
      throw new BadRequestException("Failed to exchange OAuth code for tokens");
    }

    ObjectMapper mapper = new ObjectMapper();
    JsonNode tokenJson = mapper.readTree(tokenResponse.body());

    String accessToken = tokenJson.get("access_token").asText();
    String refreshToken = tokenJson.get("refresh_token").asText();
    String idToken = tokenJson.get("id_token").asText();

    log.info("Successfully obtained tokens from Keycloak");

    // 2. Decode ID token to get user info
    Jwt jwt = jwtDecoder.decode(idToken);
    String email = extractEmailFromJwt(jwt);
    String firstName = extractFirstNameFromJwt(jwt);
    String lastName = extractLastNameFromJwt(jwt);

    log.info("Extracted user info from JWT: email={}, firstName={}, lastName={}", email, firstName, lastName);

    // 3. Find or create user in database
    User user = userRepository.findByEmail(email)
        .orElseGet(() -> createUserFromOAuth(email, firstName, lastName));

    // 4. Set security context
    setSecurityContext(user, accessToken);

    // 5. Set cookies
    addTokenCookies(accessToken, refreshToken);

    log.info("OAuth login successful for user: {}", email);

    return AuthResponse.builder()
        .tokenType("Bearer")
        .expiresIn((long) accessTokenExpiration)
        .user(userMapper.toResponse(user))
        .build();
  }

  private AuthResponse handleDirectGoogleOAuthCallback(String code) throws IOException, InterruptedException {
    log.info("Processing direct Google OAuth callback");

    // 1. Exchange code for tokens directly with Google
    String tokenUrl = "https://oauth2.googleapis.com/token";

    String form = "client_id=" + URLEncoder.encode(googleClientId, StandardCharsets.UTF_8)
        + "&client_secret=" + URLEncoder.encode(googleClientSecret, StandardCharsets.UTF_8)
        + "&grant_type=authorization_code"
        + "&code=" + URLEncoder.encode(code, StandardCharsets.UTF_8)
        + "&redirect_uri="
        + URLEncoder.encode("http://localhost:8080/profile/api/v1/auth/oauth/callback", StandardCharsets.UTF_8);

    HttpRequest httpRequest = HttpRequest.newBuilder()
        .uri(URI.create(tokenUrl))
        .header("Content-Type", "application/x-www-form-urlencoded")
        .POST(HttpRequest.BodyPublishers.ofString(form))
        .build();

    HttpClient client = HttpClient.newHttpClient();
    HttpResponse<String> tokenResponse = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

    if (tokenResponse.statusCode() != 200) {
      log.error("Failed to exchange code for tokens with Google. HTTP status: {}", tokenResponse.statusCode());
      log.error("Response body: {}", tokenResponse.body());
      throw new BadRequestException("Failed to exchange OAuth code for tokens with Google");
    }

    ObjectMapper mapper = new ObjectMapper();
    JsonNode tokenJson = mapper.readTree(tokenResponse.body());

    String accessToken = tokenJson.get("access_token").asText();
    String idToken = tokenJson.get("id_token").asText();

    log.info("Successfully obtained tokens from Google");

    // 2. Get user info from Google
    String userInfoUrl = "https://www.googleapis.com/oauth2/v2/userinfo";
    HttpRequest userInfoRequest = HttpRequest.newBuilder()
        .uri(URI.create(userInfoUrl))
        .header("Authorization", "Bearer " + accessToken)
        .GET()
        .build();

    HttpResponse<String> userInfoResponse = client.send(userInfoRequest, HttpResponse.BodyHandlers.ofString());

    if (userInfoResponse.statusCode() != 200) {
      log.error("Failed to get user info from Google. HTTP status: {}", userInfoResponse.statusCode());
      throw new BadRequestException("Failed to get user info from Google");
    }

    JsonNode userInfo = mapper.readTree(userInfoResponse.body());
    String email = userInfo.get("email").asText();
    String firstName = userInfo.get("given_name").asText();
    String lastName = userInfo.get("family_name").asText();

    log.info("Extracted user info from Google: email={}, firstName={}, lastName={}", email, firstName, lastName);

    // 3. Find or create user in database
    User user = userRepository.findByEmail(email)
        .orElseGet(() -> createUserFromOAuth(email, firstName, lastName));

    // 4. Generate JWT tokens (simplified for direct OAuth)
    String jwtToken = generateJwtToken(user);
    String refreshToken = generateRefreshToken(user);

    // 5. Set security context
    setSecurityContext(user, jwtToken);

    // 6. Set cookies
    addTokenCookies(jwtToken, refreshToken);

    log.info("Direct Google OAuth login successful for user: {}", email);

    return AuthResponse.builder()
        .tokenType("Bearer")
        .expiresIn((long) accessTokenExpiration)
        .user(userMapper.toResponse(user))
        .build();
  }

  private String generateJwtToken(User user) {
    // Simplified JWT generation - in production, use proper JWT library
    return "jwt-token-" + user.getId() + "-" + System.currentTimeMillis();
  }

  private String generateRefreshToken(User user) {
    // Simplified refresh token generation
    return "refresh-token-" + user.getId() + "-" + System.currentTimeMillis();
  }

  private User createUserFromOAuth(String email, String firstName, String lastName) {
    log.info("Creating new user from OAuth: email={}, firstName={}, lastName={}", email, firstName, lastName);

    User user = new User();
    user.setEmail(email);
    user.setFirstName(firstName);
    user.setLastName(lastName);
    user.setUsername(email);
    user.setRole(User.UserRole.CANDIDATE);
    user.setActive(true);

    User savedUser = userRepository.save(user);
    log.info("Created new user with ID: {}", savedUser.getId());

    return savedUser;
  }

  private String extractEmailFromJwt(Jwt jwt) {
    String email = jwt.getClaimAsString("email");
    if (email == null) {
      throw new BadRequestException("Email not found in OAuth token");
    }
    return email;
  }

  private String extractFirstNameFromJwt(Jwt jwt) {
    String firstName = jwt.getClaimAsString("given_name");
    if (firstName == null) {
      firstName = jwt.getClaimAsString("name");
      if (firstName != null && firstName.contains(" ")) {
        firstName = firstName.split(" ")[0];
      }
    }
    return firstName != null ? firstName : "Unknown";
  }

  private String extractLastNameFromJwt(Jwt jwt) {
    String lastName = jwt.getClaimAsString("family_name");
    if (lastName == null) {
      String name = jwt.getClaimAsString("name");
      if (name != null && name.contains(" ")) {
        String[] parts = name.split(" ");
        if (parts.length > 1) {
          lastName = parts[parts.length - 1];
        }
      }
    }
    return lastName != null ? lastName : "User";
  }

  private void addTokenCookies(String accessToken, String refreshToken) {
    // Access token cookie
    Cookie accessCookie = new Cookie("access_token", accessToken);
    accessCookie.setHttpOnly(httpOnlyCookie);
    accessCookie.setSecure(secureCookie);
    accessCookie.setPath("/");
    accessCookie.setMaxAge(accessTokenExpiration);
    if (cookieDomain != null && !cookieDomain.isEmpty()) {
      accessCookie.setDomain(cookieDomain);
    }

    // Refresh token cookie
    Cookie refreshCookie = new Cookie("refresh_token", refreshToken);
    refreshCookie.setHttpOnly(httpOnlyCookie);
    refreshCookie.setSecure(secureCookie);
    refreshCookie.setPath("/");
    refreshCookie.setMaxAge(refreshTokenExpiration);
    if (cookieDomain != null && !cookieDomain.isEmpty()) {
      refreshCookie.setDomain(cookieDomain);
    }

    response.addCookie(accessCookie);
    response.addCookie(refreshCookie);

    log.debug("Added token cookies to response");
  }

  private void setSecurityContext(User user, String accessToken) {
    // Create authentication object
    Authentication authentication = new UsernamePasswordAuthenticationToken(
        user.getId().toString(),
        accessToken,
        Arrays.asList(new SimpleGrantedAuthority("ROLE_" + user.getRole())));

    // Set in security context
    SecurityContextHolder.getContext().setAuthentication(authentication);
    log.debug("Set security context for user: {}", user.getEmail());
  }
}