package com.jobhunt.repository;

import com.jobhunt.model.entity.JobCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobCategoryRepository extends JpaRepository<JobCategory, Long> {

  List<JobCategory> findByActiveTrue();

  Optional<JobCategory> findByNameIgnoreCase(String name);

  @Query("SELECT c FROM JobCategory c WHERE c.active = true AND LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
  List<JobCategory> findByKeyword(@Param("keyword") String keyword);

  boolean existsByNameIgnoreCase(String name);
}