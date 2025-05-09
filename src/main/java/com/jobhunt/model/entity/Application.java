package com.jobhunt.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "applications")
@Data
public class Application {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne
  @JoinColumn(name = "job_id", nullable = false)
  private Job job;

  @Column(name = "cv_url")
  private String cvUrl;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String coverLetter;

  @Column(name = "expected_salary")
  private Double expectedSalary;

  @Column(name = "application_status", nullable = false)
  @Enumerated(EnumType.STRING)
  private ApplicationStatus status = ApplicationStatus.PENDING;

  @Column(name = "employer_notes", columnDefinition = "TEXT")
  private String employerNotes;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  public enum ApplicationStatus {
    PENDING,
    REVIEWING,
    SHORTLISTED,
    INTERVIEWED,
    OFFERED,
    REJECTED,
    WITHDRAWN,
    ACCEPTED
  }
}