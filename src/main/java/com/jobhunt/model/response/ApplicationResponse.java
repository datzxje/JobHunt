package com.jobhunt.model.response;

import com.jobhunt.model.entity.Application;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationResponse {
  private Long id;
  private String coverLetter;
  private String cvUrl;
  private Double expectedSalary;
  private Application.ApplicationStatus status;
  private String rejectionReason;
  private LocalDateTime interviewDate;
  private String interviewLocation;
  private String interviewNotes;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private String candidateProfile;

  // Job information - only essential fields
  private JobInfo job;

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class JobInfo {
    private Long id;
    private String title;
    private String location;
    private CompanyInfo company;
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class CompanyInfo {
    private Long id;
    private String name;
    private String logoUrl;
  }

  public static ApplicationResponse fromEntity(Application application) {
    if (application == null) {
      return null;
    }

    ApplicationResponseBuilder builder = ApplicationResponse.builder()
        .id(application.getId())
        .coverLetter(application.getCoverLetter())
        .cvUrl(application.getCvUrl())
        .expectedSalary(application.getExpectedSalary())
        .status(application.getStatus())
        .rejectionReason(application.getRejectionReason())
        .interviewDate(application.getInterviewDate())
        .interviewLocation(application.getInterviewLocation())
        .interviewNotes(application.getInterviewNotes())
        .createdAt(application.getCreatedAt())
        .updatedAt(application.getUpdatedAt())
        .candidateProfile(application.getCandidateProfile());

    // Add job information if available
    if (application.getJob() != null) {
      JobInfo jobInfo = JobInfo.builder()
          .id(application.getJob().getId())
          .title(application.getJob().getTitle())
          .location(application.getJob().getLocation())
          .build();

      // Add company information if available
      if (application.getJob().getCompany() != null) {
        CompanyInfo companyInfo = CompanyInfo.builder()
            .id(application.getJob().getCompany().getId())
            .name(application.getJob().getCompany().getName())
            .logoUrl(application.getJob().getCompany().getLogoUrl())
            .build();
        jobInfo.setCompany(companyInfo);
      }

      builder.job(jobInfo);
    }

    return builder.build();
  }
}