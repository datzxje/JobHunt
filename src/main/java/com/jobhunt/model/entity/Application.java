package com.jobhunt.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jobhunt.model.entity.Application.ApplicationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "applications")
public class Application {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  @JsonIgnore
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "job_id", nullable = false)
  @JsonIgnore
  private Job job;

  @Column(name = "cover_letter", columnDefinition = "TEXT")
  private String coverLetter;

  @Column(name = "cv_url")
  private String cvUrl;

  @Column(name = "expected_salary")
  private Double expectedSalary;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private ApplicationStatus status;

  @Column(name = "rejection_reason", columnDefinition = "TEXT")
  private String rejectionReason;

  @Column(name = "interview_date")
  private LocalDateTime interviewDate;

  @Column(name = "interview_location")
  private String interviewLocation;

  @Column(name = "interview_notes", columnDefinition = "TEXT")
  private String interviewNotes;

  @Column(name = "created_at", nullable = false, updatable = false)
  @CreationTimestamp
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  @UpdateTimestamp
  private LocalDateTime updatedAt;

  // Store all candidate requirements data in a single JSON field
  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "candidate_profile")
  private String candidateProfile;

  public enum ApplicationStatus {
    PENDING,
    REVIEWING,
    SHORTLISTED,
    INTERVIEW_SCHEDULED,
    INTERVIEWED,
    OFFER_EXTENDED,
    HIRED,
    REJECTED,
    WITHDRAWN
  }
}