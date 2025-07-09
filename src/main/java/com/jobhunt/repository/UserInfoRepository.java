package com.jobhunt.repository;

import com.jobhunt.model.entity.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserInfoRepository extends JpaRepository<UserInfo, Long> {

  /**
   * Find UserInfo by user ID
   */
  Optional<UserInfo> findByUserId(Long userId);

  /**
   * Check if UserInfo exists for a user
   */
  boolean existsByUserId(Long userId);

  /**
   * Delete UserInfo by user ID
   */
  void deleteByUserId(Long userId);

  /**
   * Find users with completed profiles
   */
  @Query("SELECT ui FROM UserInfo ui WHERE ui.isProfileComplete = true")
  Optional<UserInfo> findUsersWithCompletedProfiles();

  /**
   * Find users by completion percentage range
   */
  @Query("SELECT ui FROM UserInfo ui WHERE ui.completionPercentage >= :minPercentage AND ui.completionPercentage <= :maxPercentage")
  Optional<UserInfo> findUsersByCompletionPercentageRange(
      @Param("minPercentage") Integer minPercentage,
      @Param("maxPercentage") Integer maxPercentage);

  /**
   * Count users by completion status
   */
  @Query("SELECT COUNT(ui) FROM UserInfo ui WHERE ui.isProfileComplete = :isComplete")
  long countByProfileComplete(@Param("isComplete") Boolean isComplete);
}