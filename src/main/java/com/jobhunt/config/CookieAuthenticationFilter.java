package com.jobhunt.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@Component
@Slf4j
public class CookieAuthenticationFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    String requestURI = request.getRequestURI();
    log.debug("Processing request: {} {}", request.getMethod(), requestURI);

    // Skip filter for auth endpoints
    if (requestURI.contains("/api/v1/auth/login") ||
        requestURI.contains("/api/v1/auth/signup") ||
        requestURI.contains("/api/v1/auth/refresh-token") ||
        requestURI.contains("/api/v1/auth/logout")) {
      log.debug("Skipping cookie filter for auth endpoint: {}", requestURI);
      filterChain.doFilter(request, response);
      return;
    }

    // Log all cookies for debugging
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
      log.debug("Request has {} cookies", cookies.length);
      for (Cookie cookie : cookies) {
        log.debug("Cookie: {} = {} (domain: {}, path: {})",
            cookie.getName(),
            cookie.getValue().length() > 10 ? cookie.getValue().substring(0, 10) + "..." : cookie.getValue(),
            cookie.getDomain(),
            cookie.getPath());
      }
    } else {
      log.debug("No cookies found in request");
    }

    // Extract access token from cookie
    Optional<String> accessToken = extractTokenFromCookie(request, "access_token");

    // If access token is present in a cookie, add it to the Authorization header
    if (accessToken.isPresent()) {
      log.debug("Found access token in cookie, adding to Authorization header");
      HttpServletRequest wrappedRequest = new AuthorizationHeaderWrapper(request, "Bearer " + accessToken.get());
      filterChain.doFilter(wrappedRequest, response);
      return;
    } else {
      log.debug("No access token found in cookies for request: {}", requestURI);
    }

    filterChain.doFilter(request, response);
  }

  private Optional<String> extractTokenFromCookie(HttpServletRequest request, String cookieName) {
    Cookie[] cookies = request.getCookies();
    if (cookies == null) {
      log.debug("No cookies array found in request");
      return Optional.empty();
    }

    Optional<String> tokenValue = Arrays.stream(cookies)
        .filter(cookie -> cookieName.equals(cookie.getName()))
        .map(Cookie::getValue)
        .findFirst();

    if (tokenValue.isPresent()) {
      log.debug("Found cookie '{}' with value length: {}", cookieName, tokenValue.get().length());
    } else {
      log.debug("Cookie '{}' not found", cookieName);
    }

    return tokenValue;
  }

  // Wrapper to override the getHeader() method
  private static class AuthorizationHeaderWrapper extends HttpServletRequestWrapper {
    private final String authorizationHeader;

    public AuthorizationHeaderWrapper(HttpServletRequest request, String authorizationHeader) {
      super(request);
      this.authorizationHeader = authorizationHeader;
    }

    @Override
    public String getHeader(String name) {
      if ("Authorization".equalsIgnoreCase(name)) {
        return authorizationHeader;
      }
      return super.getHeader(name);
    }
  }
}