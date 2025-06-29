package com.jobhunt.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
@Getter
@Setter
public class Review {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "reviewer_id", nullable = false)
  private User reviewer;

  @ManyToOne
  @JoinColumn(name = "reviewed_user_id")
  private User reviewedUser;

  @ManyToOne
  @JoinColumn(name = "company_id")
  private Company company;

  @Column(nullable = false)
  private Integer rating;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String comment;

  @Column(name = "review_type", nullable = false)
  @Enumerated(EnumType.STRING)
  private ReviewType reviewType;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  public enum ReviewType {
    SERVICE_PROVIDER,
    COMPANY
  }
}