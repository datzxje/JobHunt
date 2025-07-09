package com.jobhunt.repository;

import com.jobhunt.model.entity.Job;
import com.jobhunt.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface JobRepository extends JpaRepository<Job, Long>, JpaSpecificationExecutor<Job> {
        Page<Job> findByCompanyIdAndActiveTrueAndExpiredFalse(Long companyId, Pageable pageable);

        Page<Job> findByActiveTrueAndExpiredFalse(Pageable pageable);

        Page<Job> findByActiveTrueAndExpiredFalseOrderByCreatedAtDesc(Pageable pageable);

        @Query("SELECT j FROM Job j JOIN j.applications a WHERE a.user = :user AND j.active = true AND j.expired = false")
        Page<Job> findByApplicationsUserAndActiveTrueAndExpiredFalse(@Param("user") User user, Pageable pageable);

        @Query("SELECT j FROM Job j WHERE j.assignedTo = :user AND j.active = true AND j.expired = false")
        Page<Job> findByAssignedToAndActiveTrueAndExpiredFalse(@Param("user") User assignedTo, Pageable pageable);

        @Query("SELECT j FROM Job j LEFT JOIN FETCH j.company WHERE j.id = :id AND j.active = true AND j.expired = false")
        Optional<Job> findByIdWithCompanyAndActiveTrueAndExpiredFalse(@Param("id") Long id);

        @Query("SELECT COUNT(j) FROM Job j WHERE j.company.id = :companyId AND j.active = true AND j.expired = false")
        Long countActiveJobsByCompanyId(@Param("companyId") Long companyId);

        // Method to find jobs that expire TOMORROW (for email notifications 1 day
        // before)
        // Jobs that expire tomorrow (send reminder today)
        @Query("SELECT j FROM Job j LEFT JOIN FETCH j.company WHERE j.active = true AND j.expired = false " +
                        "AND j.applicationDeadline IS NOT NULL " +
                        "AND DATE(j.applicationDeadline) = :tomorrow")
        List<Job> findJobsExpiringTomorrow(@Param("tomorrow") LocalDate tomorrow);

        // Method to find and mark expired jobs (expired = deadline < today)
        @Query("SELECT j FROM Job j WHERE j.active = true AND j.expired = false " +
                        "AND j.applicationDeadline IS NOT NULL AND DATE(j.applicationDeadline) < :today")
        List<Job> findExpiredJobs(@Param("today") LocalDate today);

        // Method to find jobs that expired YESTERDAY (for email notifications)
        // Jobs that expired yesterday but haven't been notified yet
        @Query("SELECT j FROM Job j LEFT JOIN FETCH j.company WHERE j.active = true AND j.expired = false " +
                        "AND j.applicationDeadline IS NOT NULL " +
                        "AND DATE(j.applicationDeadline) = :yesterday")
        List<Job> findJobsExpiredYesterday(@Param("yesterday") LocalDate yesterday);

        // Method for admin to view expired jobs by company
        Page<Job> findByCompanyIdAndActiveTrueAndExpiredTrue(Long companyId, Pageable pageable);
}