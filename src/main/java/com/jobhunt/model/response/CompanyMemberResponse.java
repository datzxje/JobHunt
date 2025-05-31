package com.jobhunt.model.response;

import com.jobhunt.model.entity.CompanyMember;
import lombok.Data;

import java.time.Instant;

@Data
public class CompanyMemberResponse {
  private Long id;
  private Long userId;
  private String userName;
  private String userEmail;
  private String userProfilePicture;
  private String userPhoneNumber;
  private Long companyId;
  private String companyName;
  private CompanyMember.MemberRole role;
  private String department;
  private CompanyMember.MemberStatus status;
  private Instant joinedAt;
  private Instant createdAt;
  private Instant updatedAt;
}