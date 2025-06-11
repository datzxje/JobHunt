package com.jobhunt.repository;

import com.jobhunt.model.entity.JobRequirement;
import com.jobhunt.model.entity.RequirementType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobRequirementRepository extends JpaRepository<JobRequirement, Long> {

  // Find all requirements for a specific job
  List<JobRequirement> findByJobIdOrderByWeightDesc(Long jobId);

  // Find requirements by job and type
  List<JobRequirement> findByJobIdAndType(Long jobId, RequirementType type);

  // Find a specific requirement by job, type (if you want to ensure uniqueness)
  Optional<JobRequirement> findByJobIdAndTypeAndId(Long jobId, RequirementType type, Long id);

  // Find mandatory requirements for a job
  List<JobRequirement> findByJobIdAndIsMandatoryTrue(Long jobId);

  // Find requirements with minimum weight
  @Query("SELECT jr FROM JobRequirement jr WHERE jr.job.id = :jobId AND jr.weight >= :minWeight")
  List<JobRequirement> findByJobIdAndMinWeight(@Param("jobId") Long jobId, @Param("minWeight") Integer minWeight);

  // For future ranking system - find jobs with specific requirement types
  @Query("SELECT DISTINCT jr.job.id FROM JobRequirement jr WHERE jr.type = :type AND jr.weight >= :minWeight")
  List<Long> findJobIdsByTypeAndMinWeight(@Param("type") RequirementType type, @Param("minWeight") Integer minWeight);

  // Delete by job ID (for cascade operations)
  void deleteByJobId(Long jobId);

  // Check if requirement exists for job and type
  boolean existsByJobIdAndType(Long jobId, RequirementType type);
}