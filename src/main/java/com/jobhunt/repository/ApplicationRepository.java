package com.jobhunt.repository;

import com.jobhunt.model.entity.Application;
import com.jobhunt.model.entity.Job;
import com.jobhunt.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
  boolean existsByUserAndJob(User user, Job job);

  @Query("SELECT a FROM Application a LEFT JOIN FETCH a.job j LEFT JOIN FETCH j.company WHERE a.user = :user")
  Page<Application> findByUserWithJobAndCompany(@Param("user") User user, Pageable pageable);

  Page<Application> findByUser(User user, Pageable pageable);

  Page<Application> findByJob(Job job, Pageable pageable);

  List<Application> findByJobId(Long jobId);

  long countByJobId(Long jobId);
}