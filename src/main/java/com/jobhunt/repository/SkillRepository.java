package com.jobhunt.repository;

import com.jobhunt.model.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {

  List<Skill> findByActiveTrue();

  Optional<Skill> findByNameIgnoreCase(String name);

  @Query("SELECT s FROM Skill s WHERE s.active = true AND LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
  List<Skill> findByKeyword(@Param("keyword") String keyword);

  boolean existsByNameIgnoreCase(String name);
}