package com.jobhunt.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true)
  private String username;

  @Column(unique = true)
  private String email;

  @Column(name = "first_name", nullable = false)
  private String firstName;

  @Column(name = "last_name", nullable = false)
  private String lastName;

  @Column(name = "phone_number")
  private String phoneNumber;

  @Column(name = "profile_picture_url")
  private String profilePictureUrl;

  @Column(name = "keycloak_id")
  private String keycloakId;

  @Column(name = "refresh_token")
  private String refreshToken;

  @Column(name = "is_active")
  private boolean active = true;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private Instant updatedAt;

  @OneToMany(mappedBy = "user")
  private Set<Review> reviewsGiven = new HashSet<>();

  @OneToMany(mappedBy = "user")
  private Set<Application> applications = new HashSet<>();
}