package com.jobhunt.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Table(name = "company_members", uniqueConstraints = @UniqueConstraint(columnNames = { "user_id", "company_id" }))
@Data
public class CompanyMember {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "company_id", nullable = false)
  private Company company;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private MemberRole role = MemberRole.HR;

  @Column(nullable = false)
  private String department = "Human Resources";

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private MemberStatus status = MemberStatus.ACTIVE;

  @Column(name = "joined_at")
  private Instant joinedAt;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private Instant updatedAt;

  public enum MemberRole {
    ADMIN,
    HR
  }

  public enum MemberStatus {
    ACTIVE,
    INACTIVE
  }

  @PrePersist
  public void prePersist() {
    if (joinedAt == null) {
      joinedAt = Instant.now();
    }
    if (role == MemberRole.ADMIN) {
      department = "Management";
    }
  }

  @PreUpdate
  public void preUpdate() {
    if (role == MemberRole.ADMIN) {
      department = "Management";
    }
  }
}