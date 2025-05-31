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

    // Skip filter for auth endpoints
    String requestURI = request.getRequestURI();
    if (requestURI.contains("/api/v1/auth/login") ||
        requestURI.contains("/api/v1/auth/signup") ||
        requestURI.contains("/api/v1/auth/refresh-token") ||
        requestURI.contains("/api/v1/auth/logout") ||
        requestURI.contains("/api/v1/auth/me")) {
      filterChain.doFilter(request, response);
      return;
    }

    // Extract access token from cookie
    Optional<String> accessToken = extractTokenFromCookie(request, "access_token");

    // If access token is present in a cookie, add it to the Authorization header
    if (accessToken.isPresent()) {
      log.debug("Found access token in cookie, adding to Authorization header");
      HttpServletRequest wrappedRequest = new AuthorizationHeaderWrapper(request, "Bearer " + accessToken.get());
      filterChain.doFilter(wrappedRequest, response);
      return;
    }

    filterChain.doFilter(request, response);
  }

  private Optional<String> extractTokenFromCookie(HttpServletRequest request, String cookieName) {
    Cookie[] cookies = request.getCookies();
    if (cookies == null) {
      return Optional.empty();
    }

    return Arrays.stream(cookies)
        .filter(cookie -> cookieName.equals(cookie.getName()))
        .map(Cookie::getValue)
        .findFirst();
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