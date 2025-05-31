package com.jobhunt.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "jobs")
@Data
public class Job {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String title;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String description;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String requirements;

  @Column(name = "salary_min")
  private BigDecimal salaryMin;

  @Column(name = "salary_max")
  private BigDecimal salaryMax;

  @Column(name = "employment_type", nullable = false)
  @Enumerated(EnumType.STRING)
  private EmploymentType employmentType;

  @Column(name = "experience_level")
  private String experienceLevel;

  @Column(nullable = false)
  private String location;

  @Column(name = "is_remote")
  private boolean isRemote;

  @Column(name = "application_deadline")
  private LocalDateTime applicationDeadline;

  @Column(nullable = false)
  private boolean active = true;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "company_id", nullable = false)
  private Company company;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "posted_by")
  private User postedBy;

  @OneToMany(mappedBy = "job", cascade = CascadeType.ALL)
  private Set<Application> applications = new HashSet<>();

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private Instant updatedAt;

  @OneToMany(mappedBy = "job")
  private Set<SavedJob> savedByUsers = new HashSet<>();

  public enum EmploymentType {
    FULL_TIME,
    PART_TIME,
    CONTRACT,
    INTERNSHIP,
    TEMPORARY
  }
}