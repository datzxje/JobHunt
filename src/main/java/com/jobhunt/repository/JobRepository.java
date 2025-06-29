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

import java.util.List;
import java.util.Optional;

@Repository
public interface JobRepository extends JpaRepository<Job, Long>, JpaSpecificationExecutor<Job> {
    Page<Job> findByCompanyIdAndActiveTrue(Long companyId, Pageable pageable);

    Page<Job> findByActiveTrue(Pageable pageable);

    Page<Job> findByActiveTrueOrderByCreatedAtDesc(Pageable pageable);

    Page<Job> findByApplicationsUser(User user, Pageable pageable);

    @Query("SELECT j FROM Job j LEFT JOIN FETCH j.company WHERE j.id = :id")
    Optional<Job> findByIdWithCompany(@Param("id") Long id);

    @Query("SELECT COUNT(j) FROM Job j WHERE j.company.id = :companyId AND j.active = true")
    Long countActiveJobsByCompanyId(@Param("companyId") Long companyId);
}