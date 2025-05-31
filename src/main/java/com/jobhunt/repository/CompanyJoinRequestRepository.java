package com.jobhunt.repository;

import com.jobhunt.model.entity.CompanyJoinRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyJoinRequestRepository extends JpaRepository<CompanyJoinRequest, Long> {

  Page<CompanyJoinRequest> findByCompanyIdOrderByCreatedAtDesc(Long companyId, Pageable pageable);

  Page<CompanyJoinRequest> findByCompanyIdAndStatusOrderByCreatedAtDesc(
      Long companyId,
      CompanyJoinRequest.RequestStatus status,
      Pageable pageable);

  long countByCompanyIdAndStatus(Long companyId, CompanyJoinRequest.RequestStatus status);

  boolean existsByUserIdAndCompanyId(Long userId, Long companyId);

  Optional<CompanyJoinRequest> findByUserIdAndCompanyId(Long userId, Long companyId);

  @Query("SELECT cjr FROM CompanyJoinRequest cjr " +
      "JOIN FETCH cjr.user u " +
      "WHERE cjr.company.id = :companyId " +
      "ORDER BY cjr.createdAt DESC")
  List<CompanyJoinRequest> findByCompanyIdWithUser(@Param("companyId") Long companyId);

  @Query("SELECT COUNT(cjr) FROM CompanyJoinRequest cjr " +
      "WHERE cjr.company.id = :companyId AND cjr.status = 'PENDING'")
  long countPendingRequestsByCompanyId(@Param("companyId") Long companyId);
}