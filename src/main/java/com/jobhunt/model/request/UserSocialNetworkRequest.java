package com.jobhunt.model.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSocialNetworkRequest {

  @Size(max = 200, message = "Facebook URL cannot exceed 200 characters")
  private String facebookUrl;

  @Size(max = 200, message = "Twitter URL cannot exceed 200 characters")
  private String twitterUrl;

  @Size(max = 200, message = "LinkedIn URL cannot exceed 200 characters")
  private String linkedinUrl;

  @Size(max = 200, message = "Google Plus URL cannot exceed 200 characters")
  private String googlePlusUrl;
}