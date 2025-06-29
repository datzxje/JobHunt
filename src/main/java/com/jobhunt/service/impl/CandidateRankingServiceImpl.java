package com.jobhunt.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobhunt.model.entity.Application;
import com.jobhunt.model.entity.Job;
// Removed JobRequirement entity - now using JSON requirements
// RequirementType enum no longer needed - using JSON string types
import com.jobhunt.model.response.CandidateRankingSimpleResponse;
import com.jobhunt.repository.ApplicationRepository;
import com.jobhunt.repository.JobRepository;
import com.jobhunt.service.CandidateRankingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CandidateRankingServiceImpl implements CandidateRankingService {

  private final ApplicationRepository applicationRepository;
  private final JobRepository jobRepository;
  private final ObjectMapper objectMapper;

  @Override
  @Transactional(readOnly = true)
  public List<CandidateRankingSimpleResponse> rankCandidatesForJob(Long jobId) {
    List<Application> applications = applicationRepository.findByJobId(jobId);
    if (applications.isEmpty()) {
      return Collections.emptyList();
    }

    // Get job and parse requirements from JSON
    var job = jobRepository.findById(jobId)
        .orElseThrow(() -> new RuntimeException("Job not found"));

    List<JsonNode> requirements = parseJobRequirements(job.getJobRequirements());
    if (requirements.isEmpty()) {
      return applications.stream()
          .map(app -> new CandidateRankingSimpleResponse(
              app.getUser().getId(),
              app.getUser().getFirstName() + " " + app.getUser().getLastName(),
              0.0,
              0))
          .collect(Collectors.toList());
    }

    // Calculate scores for each candidate
    List<CandidateRankingSimpleResponse> rankings = new ArrayList<>();
    for (Application application : applications) {
      try {
        JsonNode candidateProfile = objectMapper.readTree(application.getCandidateProfile());

        double averageScore = calculateAverageScore(candidateProfile, requirements);

        rankings.add(new CandidateRankingSimpleResponse(
            application.getUser().getId(),
            application.getUser().getFirstName() + " " + application.getUser().getLastName(),
            averageScore,
            0));
      } catch (Exception e) {
        log.error("Error processing candidate profile for user {}: {}",
            application.getUser().getId(), e.getMessage());
      }
    }

    rankings.sort((a, b) -> Double.compare(b.getAverageScore(), a.getAverageScore()));
    for (int i = 0; i < rankings.size(); i++) {
      rankings.get(i).setRank(i + 1);
    }

    return rankings;
  }

  private List<JsonNode> parseJobRequirements(String jobRequirementsJson) {
    try {
      if (jobRequirementsJson == null || jobRequirementsJson.trim().isEmpty()) {
        return Collections.emptyList();
      }
      JsonNode requirementsArray = objectMapper.readTree(jobRequirementsJson);
      List<JsonNode> requirements = new ArrayList<>();
      if (requirementsArray.isArray()) {
        requirementsArray.forEach(requirements::add);
      }
      return requirements;
    } catch (Exception e) {
      log.error("Error parsing job requirements JSON: {}", e.getMessage());
      return Collections.emptyList();
    }
  }

  private double calculateAverageScore(JsonNode candidateProfile, List<JsonNode> requirements) {
    double totalScore = 0;
    double totalWeight = 0;

    for (JsonNode requirement : requirements) {
      double score = scoreRequirement(candidateProfile, requirement);
      int weight = requirement.get("weight").asInt(5);
      totalScore += score * weight;
      totalWeight += weight;
    }

    return totalWeight > 0 ? totalScore / totalWeight : 0;
  }

  private double scoreRequirement(JsonNode candidateProfile, JsonNode requirement) {
    try {
      JsonNode criteriaData = requirement.get("data");
      String requirementType = requirement.get("type").asText().toUpperCase();

      switch (requirementType) {
        case "EXPERIENCE":
          return scoreExperience(candidateProfile, criteriaData);
        case "SKILLS":
          return scoreSkills(candidateProfile, criteriaData);
        case "EDUCATION":
          return scoreEducation(candidateProfile, criteriaData);
        case "LANGUAGES":
          return scoreLanguages(candidateProfile, criteriaData);
        case "AGE":
          return scoreAge(candidateProfile, criteriaData);
        case "SALARY":
          return scoreSalary(candidateProfile, criteriaData);
        case "LOCATION":
          return scoreLocation(candidateProfile, criteriaData);
        case "AVAILABILITY":
          return scoreAvailability(candidateProfile, criteriaData);
        default:
          return 0;
      }
    } catch (Exception e) {
      log.error("Error scoring requirement: {}", e.getMessage());
      return 0;
    }
  }

  private double scoreExperience(JsonNode candidateProfile, JsonNode criteriaData) {
    try {
      String candidateExp = candidateProfile.get("experience").asText();
      String requiredExp = criteriaData.get("minExperience").asText();

      double candidateYears = parseExperienceYears(candidateExp);
      double requiredYears = parseExperienceYears(requiredExp);

      if (candidateYears >= requiredYears) {
        return 10.0;
      } else if (candidateYears >= requiredYears * 0.7) {
        return 7.0;
      } else if (candidateYears >= requiredYears * 0.5) {
        return 5.0;
      } else {
        return 3.0;
      }
    } catch (Exception e) {
      log.error("Error scoring experience: {}", e.getMessage());
      return 0;
    }
  }

  private double scoreSkills(JsonNode candidateProfile, JsonNode criteriaData) {
    try {
      JsonNode candidateSkills = candidateProfile.get("skills");
      JsonNode requiredSkills = criteriaData.get("skills");

      if (!candidateSkills.isArray() || !requiredSkills.isArray()) {
        return 0;
      }

      Set<String> candidateSkillSet = new HashSet<>();
      for (JsonNode skill : candidateSkills) {
        candidateSkillSet.add(skill.asText().toLowerCase());
      }

      int matchedSkills = 0;
      for (JsonNode skill : requiredSkills) {
        if (candidateSkillSet.contains(skill.asText().toLowerCase())) {
          matchedSkills++;
        }
      }

      return (double) matchedSkills / requiredSkills.size() * 10;
    } catch (Exception e) {
      log.error("Error scoring skills: {}", e.getMessage());
      return 0;
    }
  }

  private double scoreEducation(JsonNode candidateProfile, JsonNode criteriaData) {
    try {
      String candidateEdu = candidateProfile.get("education").asText();
      String requiredEdu = criteriaData.get("minEducation").asText();

      Map<String, Integer> educationLevels = Map.of(
          "High School", 1,
          "Certificate", 2,
          "Diploma", 3,
          "Bachelor's Degree", 4,
          "Master's Degree", 5,
          "PhD", 6);

      int candidateLevel = educationLevels.getOrDefault(candidateEdu, 0);
      int requiredLevel = educationLevels.getOrDefault(requiredEdu, 0);

      if (candidateLevel >= requiredLevel) {
        return 10.0;
      } else if (candidateLevel >= requiredLevel - 1) {
        return 7.0;
      } else {
        return 3.0;
      }
    } catch (Exception e) {
      log.error("Error scoring education: {}", e.getMessage());
      return 0;
    }
  }

  private double scoreLanguages(JsonNode candidateProfile, JsonNode criteriaData) {
    try {
      JsonNode candidateLangs = candidateProfile.get("languages");
      JsonNode requiredLangs = criteriaData.get("languages");

      if (!candidateLangs.isArray() || !requiredLangs.isArray()) {
        return 0;
      }

      Map<String, String> candidateLangMap = new HashMap<>();
      for (JsonNode lang : candidateLangs) {
        candidateLangMap.put(
            lang.get("name").asText().toLowerCase(),
            lang.get("level").asText().toLowerCase());
      }

      int matchedLangs = 0;
      for (JsonNode lang : requiredLangs) {
        String langName = lang.get("name").asText().toLowerCase();
        String requiredLevel = lang.get("level").asText().toLowerCase();

        if (candidateLangMap.containsKey(langName)) {
          String candidateLevel = candidateLangMap.get(langName);
          if (isLanguageLevelSufficient(candidateLevel, requiredLevel)) {
            matchedLangs++;
          }
        }
      }

      return (double) matchedLangs / requiredLangs.size() * 10;
    } catch (Exception e) {
      log.error("Error scoring languages: {}", e.getMessage());
      return 0;
    }
  }

  private double scoreAge(JsonNode candidateProfile, JsonNode criteriaData) {
    try {
      int candidateAge = candidateProfile.get("age").asInt();
      int minAge = criteriaData.get("minAge").asInt();
      int maxAge = criteriaData.get("maxAge").asInt();

      if (candidateAge >= minAge && candidateAge <= maxAge) {
        return 10.0;
      } else if (candidateAge >= minAge - 2 && candidateAge <= maxAge + 2) {
        return 7.0;
      } else {
        return 3.0;
      }
    } catch (Exception e) {
      log.error("Error scoring age: {}", e.getMessage());
      return 0;
    }
  }

  private double scoreSalary(JsonNode candidateProfile, JsonNode criteriaData) {
    try {
      double candidateSalary = candidateProfile.get("expectedSalary").asDouble();
      double minSalary = criteriaData.get("minSalary").asDouble();
      double maxSalary = criteriaData.get("maxSalary").asDouble();

      if (candidateSalary >= minSalary && candidateSalary <= maxSalary) {
        return 10.0;
      } else if (candidateSalary >= minSalary * 0.9 && candidateSalary <= maxSalary * 1.1) {
        return 7.0;
      } else {
        return 3.0;
      }
    } catch (Exception e) {
      log.error("Error scoring salary: {}", e.getMessage());
      return 0;
    }
  }

  private double scoreLocation(JsonNode candidateProfile, JsonNode criteriaData) {
    try {
      String candidateLocation = candidateProfile.get("location").asText().toLowerCase();
      String requiredLocation = criteriaData.get("location").asText().toLowerCase();

      if (candidateLocation.equals(requiredLocation)) {
        return 10.0;
      } else if (candidateLocation.contains(requiredLocation) || requiredLocation.contains(candidateLocation)) {
        return 7.0;
      } else {
        return 3.0;
      }
    } catch (Exception e) {
      log.error("Error scoring location: {}", e.getMessage());
      return 0;
    }
  }

  private double scoreAvailability(JsonNode candidateProfile, JsonNode criteriaData) {
    try {
      String candidateAvailability = candidateProfile.get("availability").asText().toLowerCase();
      String requiredAvailability = criteriaData.get("availability").asText().toLowerCase();

      if (candidateAvailability.equals(requiredAvailability)) {
        return 10.0;
      } else if (candidateAvailability.contains("immediate") || requiredAvailability.contains("immediate")) {
        return 7.0;
      } else {
        return 5.0;
      }
    } catch (Exception e) {
      log.error("Error scoring availability: {}", e.getMessage());
      return 0;
    }
  }

  private double parseExperienceYears(String experience) {
    try {
      String[] parts = experience.toLowerCase().split("-");
      if (parts.length > 1) {
        return (Double.parseDouble(parts[0]) + Double.parseDouble(parts[1])) / 2;
      } else {
        return Double.parseDouble(parts[0].replaceAll("[^0-9.]", ""));
      }
    } catch (Exception e) {
      return 0;
    }
  }

  private boolean isLanguageLevelSufficient(String candidateLevel, String requiredLevel) {
    Map<String, Integer> levelMap = Map.of(
        "basic", 1,
        "intermediate", 2,
        "advanced", 3,
        "fluent", 4,
        "native", 5);

    return levelMap.getOrDefault(candidateLevel.toLowerCase(), 0) >= levelMap.getOrDefault(requiredLevel.toLowerCase(),
        0);
  }
}