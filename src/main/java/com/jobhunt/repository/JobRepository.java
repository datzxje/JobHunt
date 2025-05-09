package com.jobhunt.repository;

import com.jobhunt.model.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {
  List<Job> findByCompanyIdAndActiveTrue(Long companyId);

  List<Job> findByActiveTrue();

  @Query("""
      SELECT j FROM Job j
      WHERE j.active = true
      AND (:keyword IS NULL OR (
          LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
          OR LOWER(j.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
          OR LOWER(j.requirements) LIKE LOWER(CONCAT('%', :keyword, '%'))
      ))
      AND (:location IS NULL OR LOWER(j.location) LIKE LOWER(CONCAT('%', :location, '%')))
      AND (:employmentType IS NULL OR j.employmentType = :employmentType)
      AND (:experienceLevel IS NULL OR j.experienceLevel = :experienceLevel)
      AND (:isRemote IS NULL OR j.isRemote = :isRemote)
      """)
  List<Job> searchJobs(String keyword, String location, String employmentType,
      String experienceLevel, Boolean isRemote);
}