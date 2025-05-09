package com.jobhunt.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component("userSecurity")
public class UserSecurity {

  public boolean isCurrentUser(Long userId) {
    Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String currentUserId = jwt.getClaimAsString("sub");
    return currentUserId != null && currentUserId.equals(userId.toString());
  }
}