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

  // Company Profile Section
  @Column(name = "logo_url")
  private String logoUrl;

  @Column(name = "cover_url")
  private String coverUrl;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String email;

  @Column(name = "phone_number")
  private String phoneNumber;

  @Column(name = "website_url")
  private String websiteUrl;

  @Column(name = "establishment_year")
  private Integer establishmentYear;

  @Column(name = "team_size")
  private String teamSize;

  @Column(name = "industry_type", nullable = false)
  private String industryType;

  @Column(columnDefinition = "TEXT")
  private String about;

  // Social Network Section
  @Column(name = "facebook_url")
  private String facebookUrl;

  @Column(name = "twitter_url")
  private String twitterUrl;

  @Column(name = "linkedin_url")
  private String linkedinUrl;

  @Column(name = "google_plus_url")
  private String googlePlusUrl;

  // Contact Information Section
  private String country;
  private String city;
  private String address;
  private Double latitude;
  private Double longitude;

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