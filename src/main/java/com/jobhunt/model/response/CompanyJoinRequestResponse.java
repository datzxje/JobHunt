package com.jobhunt.model.response;

import com.jobhunt.model.entity.CompanyJoinRequest;
import lombok.Data;

import java.time.Instant;

@Data
public class CompanyJoinRequestResponse {
  private Long id;
  private Long userId;
  private String userName;
  private String userEmail;
  private String userProfilePicture;
  private Long companyId;
  private String companyName;
  private String message;
  private CompanyJoinRequest.RequestStatus status;
  private Instant requestedAt;
  private Instant reviewedAt;
  private Long reviewedBy;
  private String reviewedByName;
  private Instant createdAt;
  private Instant updatedAt;
}