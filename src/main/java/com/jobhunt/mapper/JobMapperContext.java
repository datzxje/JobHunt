package com.jobhunt.mapper;

import com.jobhunt.model.entity.JobCategory;
import com.jobhunt.model.entity.Language;
import com.jobhunt.model.entity.Skill;
import com.jobhunt.repository.JobCategoryRepository;
import com.jobhunt.repository.LanguageRepository;
import com.jobhunt.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JobMapperContext {

  private final SkillRepository skillRepository;
  private final JobCategoryRepository jobCategoryRepository;
  private final LanguageRepository languageRepository;

  public Set<JobCategory> getCategoriesByIds(Set<Long> categoryIds) {
    return categoryIds.stream()
        .map(jobCategoryRepository::findById)
        .filter(java.util.Optional::isPresent)
        .map(java.util.Optional::get)
        .collect(Collectors.toSet());
  }

  public Set<Skill> getSkillsByIds(Set<Long> skillIds) {
    return skillIds.stream()
        .map(skillRepository::findById)
        .filter(java.util.Optional::isPresent)
        .map(java.util.Optional::get)
        .collect(Collectors.toSet());
  }

  public Set<Language> getLanguagesByIds(Set<Long> languageIds) {
    return languageIds.stream()
        .map(languageRepository::findById)
        .filter(java.util.Optional::isPresent)
        .map(java.util.Optional::get)
        .collect(Collectors.toSet());
  }
}