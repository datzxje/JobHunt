package com.jobhunt.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "user_info")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", unique = true)
  @JsonBackReference
  private User user;

  // Main Profile Information
  @Column(name = "profile_picture_url")
  private String profilePictureUrl;

  @Column(name = "full_name")
  private String fullName;

  @Column(name = "job_title")
  private String jobTitle;

  @Column(name = "phone")
  private String phone;

  @Column(name = "email")
  private String email;

  @Column(name = "website")
  private String website;

  @Column(name = "current_salary")
  private String currentSalary;

  @Column(name = "expected_salary")
  private String expectedSalary;

  @Column(name = "experience")
  private String experience;

  @Column(name = "age_range")
  private String ageRange;

  @Column(name = "education_level")
  private String educationLevel;

  @Column(name = "languages")
  private String languages;

  @ElementCollection
  @CollectionTable(name = "user_categories", joinColumns = @JoinColumn(name = "user_info_id"))
  @Column(name = "category")
  private List<String> categories;

  @Column(name = "allow_search_listing")
  private Boolean allowSearchListing = true;

  @Column(name = "description", columnDefinition = "TEXT")
  private String description;

  // Social Network Information
  @Column(name = "facebook_url")
  private String facebookUrl;

  @Column(name = "twitter_url")
  private String twitterUrl;

  @Column(name = "linkedin_url")
  private String linkedinUrl;

  @Column(name = "google_plus_url")
  private String googlePlusUrl;

  // Contact Information
  @Column(name = "country")
  private String country;

  @Column(name = "city")
  private String city;

  @Column(name = "complete_address")
  private String completeAddress;

  @Column(name = "map_location")
  private String mapLocation;

  @Column(name = "latitude")
  private BigDecimal latitude;

  @Column(name = "longitude")
  private BigDecimal longitude;

  // Profile completion tracking
  @Column(name = "is_profile_complete")
  private Boolean isProfileComplete = false;

  @Column(name = "completion_percentage")
  private Integer completionPercentage = 0;

  @CreationTimestamp
  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  // Helper method to calculate completion percentage
  public void updateCompletionPercentage() {
    int totalFields = 20; // Total important fields
    int completedFields = 0;

    if (fullName != null && !fullName.trim().isEmpty())
      completedFields++;
    if (jobTitle != null && !jobTitle.trim().isEmpty())
      completedFields++;
    if (phone != null && !phone.trim().isEmpty())
      completedFields++;
    if (email != null && !email.trim().isEmpty())
      completedFields++;
    if (website != null && !website.trim().isEmpty())
      completedFields++;
    if (currentSalary != null && !currentSalary.trim().isEmpty())
      completedFields++;
    if (expectedSalary != null && !expectedSalary.trim().isEmpty())
      completedFields++;
    if (experience != null && !experience.trim().isEmpty())
      completedFields++;
    if (ageRange != null && !ageRange.trim().isEmpty())
      completedFields++;
    if (educationLevel != null && !educationLevel.trim().isEmpty())
      completedFields++;
    if (languages != null && !languages.trim().isEmpty())
      completedFields++;
    if (categories != null && !categories.isEmpty())
      completedFields++;
    if (description != null && !description.trim().isEmpty())
      completedFields++;
    if (country != null && !country.trim().isEmpty())
      completedFields++;
    if (city != null && !city.trim().isEmpty())
      completedFields++;
    if (completeAddress != null && !completeAddress.trim().isEmpty())
      completedFields++;
    if (profilePictureUrl != null && !profilePictureUrl.trim().isEmpty())
      completedFields++;
    if (facebookUrl != null && !facebookUrl.trim().isEmpty())
      completedFields++;
    if (linkedinUrl != null && !linkedinUrl.trim().isEmpty())
      completedFields++;
    if (twitterUrl != null && !twitterUrl.trim().isEmpty())
      completedFields++;

    this.completionPercentage = (int) ((double) completedFields / totalFields * 100);
    this.isProfileComplete = this.completionPercentage >= 80; // 80% threshold
  }
}