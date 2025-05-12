package com.jobhunt.repository;

import com.jobhunt.model.entity.ServiceJob;
import com.jobhunt.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ServiceJobRepository extends JpaRepository<ServiceJob, Long>, JpaSpecificationExecutor<ServiceJob> {
  List<ServiceJob> findByPostedByUser(User user);

  List<ServiceJob> findByAssignedToUser(User user);

  List<ServiceJob> findByServiceType(ServiceJob.ServiceType serviceType);

  List<ServiceJob> findByStatus(ServiceJob.JobStatus status);

  List<ServiceJob> findByIsUrgent(boolean isUrgent);

  List<ServiceJob> findByActiveTrue();

  @Query("SELECT sj FROM ServiceJob sj WHERE " +
      "(:location IS NULL OR sj.location LIKE %:location%) AND " +
      "(:minBudget IS NULL OR sj.estimatedBudget >= :minBudget) AND " +
      "(:maxBudget IS NULL OR sj.estimatedBudget <= :maxBudget) AND " +
      "(:serviceType IS NULL OR sj.serviceType = :serviceType) AND " +
      "sj.active = true")
  List<ServiceJob> searchServiceJobs(
      @Param("location") String location,
      @Param("minBudget") BigDecimal minBudget,
      @Param("maxBudget") BigDecimal maxBudget,
      @Param("serviceType") ServiceJob.ServiceType serviceType);
}