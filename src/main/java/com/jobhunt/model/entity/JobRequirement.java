package com.jobhunt.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Table(name = "job_requirements")
@Data
public class JobRequirement {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "job_id", nullable = false)
  private Job job;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private RequirementType type;

  @Column(nullable = false)
  @Min(1)
  @Max(10)
  private Integer weight; // For weighted scoring (1-10)

  @Column(nullable = false)
  private Boolean isMandatory = false; // Hard requirements vs preferences

  @Column(columnDefinition = "TEXT")
  private String criteriaData; // JSON storage for flexible criteria

  @Column(length = 500)
  private String description; // Human-readable description

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private Instant updatedAt;
}