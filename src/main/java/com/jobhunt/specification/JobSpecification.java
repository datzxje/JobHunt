package com.jobhunt.specification;

import com.jobhunt.model.entity.Job;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class JobSpecification {

  public static Specification<Job> hasKeyword(String keyword) {
    return (root, query, builder) -> {
      if (keyword == null || keyword.trim().isEmpty()) {
        return builder.conjunction(); // no condition
      }

      String keywordLower = "%" + keyword.toLowerCase() + "%";

      return builder.or(
          builder.like(builder.lower(root.get("title")), keywordLower),
          builder.like(builder.lower(root.get("description")), keywordLower),
          builder.like(builder.lower(root.get("requirements")), keywordLower));
    };
  }

  public static Specification<Job> hasLocation(String location) {
    return (root, query, builder) -> {
      if (location == null || location.trim().isEmpty()) {
        return builder.conjunction();
      }
      return builder.like(builder.lower(root.get("location")),
          "%" + location.toLowerCase() + "%");
    };
  }

  public static Specification<Job> hasCity(String city) {
    return (root, query, builder) -> {
      if (city == null || city.trim().isEmpty()) {
        return builder.conjunction();
      }
      return builder.like(builder.lower(root.get("city")),
          "%" + city.toLowerCase() + "%");
    };
  }

  public static Specification<Job> hasCategories(String category) {
    return (root, query, builder) -> {
      if (category == null || category.trim().isEmpty()) {
        return builder.conjunction();
      }
      // Use PostgreSQL JSON contains operator for categories JSON array
      return builder.equal(builder.function("jsonb_path_exists", Boolean.class,
          root.get("categories"),
          builder.literal("$[*] ? (@ like_regex \"" + category + "\" flag \"i\")")), true);
    };
  }

  public static Specification<Job> hasRequiredSkills(String skill) {
    return (root, query, builder) -> {
      if (skill == null || skill.trim().isEmpty()) {
        return builder.conjunction();
      }
      // Use PostgreSQL JSON contains operator for required skills JSON array
      return builder.equal(builder.function("jsonb_path_exists", Boolean.class,
          root.get("requiredSkills"),
          builder.literal("$[*] ? (@ like_regex \"" + skill + "\" flag \"i\")")), true);
    };
  }

  public static Specification<Job> hasEmploymentType(String employmentType) {
    return (root, query, builder) -> {
      if (employmentType == null || employmentType.trim().isEmpty()) {
        return builder.conjunction();
      }
      return builder.equal(root.get("employmentType"),
          Job.EmploymentType.valueOf(employmentType.toUpperCase()));
    };
  }

  public static Specification<Job> hasExperienceLevel(String experienceLevel) {
    return (root, query, builder) -> {
      if (experienceLevel == null || experienceLevel.trim().isEmpty()) {
        return builder.conjunction();
      }
      return builder.equal(root.get("experienceLevel"), experienceLevel);
    };
  }

  public static Specification<Job> isRemote(Boolean isRemote) {
    return (root, query, builder) -> {
      if (isRemote == null) {
        return builder.conjunction();
      }
      return builder.equal(root.get("isRemote"), isRemote);
    };
  }

  public static Specification<Job> isActive(boolean active) {
    return (root, query, builder) -> builder.equal(root.get("active"), active);
  }

  public static Specification<Job> hasSalaryRange(BigDecimal minSalary, BigDecimal maxSalary) {
    return (root, query, builder) -> {
      List<Predicate> predicates = new ArrayList<>();

      if (minSalary != null) {
        // Find jobs where the job's max salary is >= user's min salary requirement
        // OR job has no salary specified (null salaries should be included)
        predicates.add(
            builder.or(
                builder.greaterThanOrEqualTo(root.get("salaryMax"), minSalary),
                builder.isNull(root.get("salaryMax"))));
      }

      if (maxSalary != null) {
        // Find jobs where the job's min salary is <= user's max salary budget
        // OR job has no salary specified (null salaries should be included)
        predicates.add(
            builder.or(
                builder.lessThanOrEqualTo(root.get("salaryMin"), maxSalary),
                builder.isNull(root.get("salaryMin"))));
      }

      if (predicates.isEmpty()) {
        return builder.conjunction(); // no condition if both are null
      }

      return builder.and(predicates.toArray(new Predicate[0]));
    };
  }

  public static Specification<Job> buildSearchSpecification(String keyword, String location,
      String employmentType, String experienceLevel,
      Boolean isRemote, String city, String category, String skill,
      BigDecimal minSalary, BigDecimal maxSalary) {
    return Specification.where(isActive(true))
        .and(hasKeyword(keyword))
        .and(hasLocation(location))
        .and(hasEmploymentType(employmentType))
        .and(hasExperienceLevel(experienceLevel))
        .and(isRemote(isRemote))
        .and(hasCity(city))
        .and(hasCategories(category))
        .and(hasRequiredSkills(skill))
        .and(hasSalaryRange(minSalary, maxSalary));
  }
}