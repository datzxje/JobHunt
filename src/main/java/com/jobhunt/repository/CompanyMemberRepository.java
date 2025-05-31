package com.jobhunt.repository;

import com.jobhunt.model.entity.CompanyMember;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyMemberRepository extends JpaRepository<CompanyMember, Long> {

    Page<CompanyMember> findByCompanyIdOrderByCreatedAtDesc(Long companyId, Pageable pageable);

    Page<CompanyMember> findByCompanyIdAndRoleOrderByCreatedAtDesc(
            Long companyId,
            CompanyMember.MemberRole role,
            Pageable pageable);

    Page<CompanyMember> findByCompanyIdAndStatusOrderByCreatedAtDesc(
            Long companyId,
            CompanyMember.MemberStatus status,
            Pageable pageable);

    long countByCompanyIdAndRole(Long companyId, CompanyMember.MemberRole role);

    long countByCompanyIdAndStatus(Long companyId, CompanyMember.MemberStatus status);

    long countByCompanyId(Long companyId);

    boolean existsByUserIdAndCompanyId(Long userId, Long companyId);

    Optional<CompanyMember> findByUserIdAndCompanyId(Long userId, Long companyId);

    @Query("SELECT cm FROM CompanyMember cm " +
            "JOIN FETCH cm.user u " +
            "WHERE cm.company.id = :companyId " +
            "ORDER BY cm.role DESC, cm.createdAt DESC")
    List<CompanyMember> findByCompanyIdWithUser(@Param("companyId") Long companyId);

    @Query("SELECT cm FROM CompanyMember cm " +
            "WHERE cm.company.id = :companyId AND cm.role = 'ADMIN' AND cm.status = 'ACTIVE'")
    List<CompanyMember> findActiveAdminsByCompanyId(@Param("companyId") Long companyId);

    @Query("SELECT COUNT(cm) FROM CompanyMember cm " +
            "WHERE cm.company.id = :companyId AND cm.role = :role AND cm.status = 'ACTIVE'")
    long countActiveByCompanyIdAndRole(@Param("companyId") Long companyId,
            @Param("role") CompanyMember.MemberRole role);
}