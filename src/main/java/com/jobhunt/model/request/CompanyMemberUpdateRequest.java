package com.jobhunt.model.request;

import com.jobhunt.model.entity.CompanyMember;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CompanyMemberUpdateRequest {

  @NotNull(message = "Role is required")
  private CompanyMember.MemberRole role;

  private String department;

  @NotNull(message = "Status is required")
  private CompanyMember.MemberStatus status;
}