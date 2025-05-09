package com.jobhunt.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "companies")
@Data
public class Company {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String description;

  @Column(name = "website_url")
  private String websiteUrl;

  @Column(nullable = false)
  private String address;

  @Column(name = "company_size")
  private Integer companySize;

  @Column(name = "establishment_year")
  private Integer establishmentYear;

  @Column(name = "industry_type", nullable = false)
  private String industryType;

  @Column(name = "tax_id", nullable = false, unique = true)
  private String taxId;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
  private Set<Job> jobs = new HashSet<>();

  @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
  private Set<Review> reviews = new HashSet<>();

  @Column(nullable = false)
  private boolean active = true;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private Instant updatedAt;
}