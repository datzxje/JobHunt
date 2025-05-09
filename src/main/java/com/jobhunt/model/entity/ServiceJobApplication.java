package com.jobhunt.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "service_job_applications")
@Data
public class ServiceJobApplication {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne
  @JoinColumn(name = "service_job_id", nullable = false)
  private ServiceJob serviceJob;

  @Column(name = "proposed_price")
  private BigDecimal proposedPrice;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String proposal;

  @Column(name = "estimated_completion_time")
  private LocalDateTime estimatedCompletionTime;

  @Column(name = "application_status", nullable = false)
  @Enumerated(EnumType.STRING)
  private ApplicationStatus status = ApplicationStatus.PENDING;

  @Column(name = "owner_notes", columnDefinition = "TEXT")
  private String ownerNotes;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  public enum ApplicationStatus {
    PENDING,
    REVIEWING,
    ACCEPTED,
    REJECTED,
    WITHDRAWN
  }
}