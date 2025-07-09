package com.jobhunt.model.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserBasicResponse {
  private Long id;
  private String email;
  private String firstName;
  private String lastName;
  private String profilePictureUrl;
  private String role;
}