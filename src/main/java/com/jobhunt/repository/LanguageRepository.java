package com.jobhunt.repository;

import com.jobhunt.model.entity.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LanguageRepository extends JpaRepository<Language, Long> {

  List<Language> findByActiveTrue();

  Optional<Language> findByNameIgnoreCase(String name);

  Optional<Language> findByIsoCode(String isoCode);

  @Query("SELECT l FROM Language l WHERE l.active = true AND (LOWER(l.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(l.isoCode) LIKE LOWER(CONCAT('%', :keyword, '%')))")
  List<Language> findByKeyword(@Param("keyword") String keyword);

  boolean existsByNameIgnoreCase(String name);

  boolean existsByIsoCode(String isoCode);
}