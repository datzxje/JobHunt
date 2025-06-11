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

  @Column(name = "career_level")
  private String careerLevel;

  @Column(nullable = false)
  private String location;

  @Column
  private String country;

  @Column
  private String city;

  @Column
  private String address;

  @Column(name = "is_remote")
  private boolean isRemote;

  @Column(name = "application_deadline")
  private LocalDateTime applicationDeadline;

  // New fields from frontend form
  @Column(name = "hours_per_week")
  private String hoursPerWeek;

  @Column(name = "gender_preference")
  @Enumerated(EnumType.STRING)
  private GenderPreference genderPreference;

  @Column(name = "minimum_qualification")
  private String minimumQualification;

  @Column(name = "minimum_age")
  private Integer minimumAge;

  @Column(name = "maximum_age")
  private Integer maximumAge;

  @Column(name = "minimum_experience_years")
  private Integer minimumExperienceYears;

  @Column(name = "maximum_experience_years")
  private Integer maximumExperienceYears;

  @Column(nullable = false)
  private boolean active = true;

  // Relationships
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "company_id", nullable = false)
  private Company company;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "posted_by")
  private User postedBy;

  @OneToMany(mappedBy = "job", cascade = CascadeType.ALL)
  private Set<Application> applications = new HashSet<>();

  @OneToMany(mappedBy = "job")
  private Set<SavedJob> savedByUsers = new HashSet<>();

  // Many-to-Many relationships
  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name = "job_categories_mapping", joinColumns = @JoinColumn(name = "job_id"), inverseJoinColumns = @JoinColumn(name = "category_id"))
  private Set<JobCategory> categories = new HashSet<>();

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name = "job_skills_mapping", joinColumns = @JoinColumn(name = "job_id"), inverseJoinColumns = @JoinColumn(name = "skill_id"))
  private Set<Skill> requiredSkills = new HashSet<>();

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name = "job_languages_mapping", joinColumns = @JoinColumn(name = "job_id"), inverseJoinColumns = @JoinColumn(name = "language_id"))
  private Set<Language> requiredLanguages = new HashSet<>();

  // Job Requirements for ranking/filtering
  @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<JobRequirement> jobRequirements = new HashSet<>();

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private Instant updatedAt;

  public enum EmploymentType {
    FULL_TIME,
    PART_TIME,
    CONTRACT,
    INTERNSHIP,
    TEMPORARY,
    FREELANCER
  }

  public enum GenderPreference {
    NO_PREFERENCE,
    MALE,
    FEMALE,
    OTHER
  }
}