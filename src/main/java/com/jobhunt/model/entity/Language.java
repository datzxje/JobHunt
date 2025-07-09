package com.jobhunt.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "languages")
@Setter
@Getter
public class Language {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String name;

  @Column(name = "iso_code", unique = true)
  private String isoCode;

  @Column(nullable = false)
  private boolean active = true;

  // Removed ManyToMany relationship - now using JSON in Job entity
  // @ManyToMany(mappedBy = "requiredLanguages")
  // private Set<Job> jobs = new HashSet<>();

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private Instant updatedAt;
}