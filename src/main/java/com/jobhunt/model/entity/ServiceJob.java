package com.jobhunt.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "service_jobs")
@Data
public class ServiceJob {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String title;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String description;

  @Column(name = "service_type", nullable = false)
  @Enumerated(EnumType.STRING)
  private ServiceType serviceType;

  @Column(nullable = false)
  private String location;

  @Column(name = "estimated_budget")
  private BigDecimal estimatedBudget;

  @Column(name = "required_completion_date")
  private LocalDateTime requiredCompletionDate;

  @Column(name = "is_urgent")
  private boolean isUrgent;

  @Column(nullable = false)
  private boolean active = true;

  @ManyToOne
  @JoinColumn(name = "posted_by_user_id", nullable = false)
  private User postedByUser;

  @ManyToOne
  @JoinColumn(name = "assigned_to_user_id")
  private User assignedToUser;

  @Column(name = "job_status", nullable = false)
  @Enumerated(EnumType.STRING)
  private JobStatus status = JobStatus.OPEN;

  @OneToMany(mappedBy = "serviceJob")
  private Set<ServiceJobApplication> applications = new HashSet<>();

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  public enum ServiceType {
    PLUMBING,
    ELECTRICAL,
    CARPENTRY,
    CLEANING,
    PAINTING,
    MOVING,
    GARDENING,
    OTHER
  }

  public enum JobStatus {
    OPEN,
    ASSIGNED,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED
  }
}