package com.jobhunt.repository;

import com.jobhunt.model.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
  Optional<Company> findByUserIdAndActiveTrue(Long userId);

  boolean existsByTaxId(String taxId);
}