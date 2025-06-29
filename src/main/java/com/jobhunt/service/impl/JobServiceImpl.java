package com.jobhunt.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobhunt.exception.BadRequestException;
import com.jobhunt.exception.ResourceNotFoundException;
import com.jobhunt.mapper.JobMapper;
import com.jobhunt.mapper.CompanyBasicMapper;
import com.jobhunt.model.entity.Application;
import com.jobhunt.model.entity.Application.ApplicationStatus;
import com.jobhunt.model.entity.Company;
import com.jobhunt.model.entity.Job;
import com.jobhunt.model.entity.SavedJob;
import com.jobhunt.model.entity.User;
import com.jobhunt.model.request.JobRequest;
import com.jobhunt.model.response.JobResponse;
import com.jobhunt.repository.ApplicationRepository;
import com.jobhunt.repository.CompanyRepository;
import com.jobhunt.repository.JobRepository;
import com.jobhunt.repository.UserRepository;
import com.jobhunt.repository.SavedJobRepository;
import com.jobhunt.service.JobService;
import com.jobhunt.specification.JobSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobServiceImpl implements JobService {

  private final JobRepository jobRepository;
  private final UserRepository userRepository;
  private final CompanyRepository companyRepository;
  private final ApplicationRepository applicationRepository;
  private final JobMapper jobMapper;
  private final SavedJobRepository savedJobRepository;
  private final ObjectMapper objectMapper;
  private final CompanyBasicMapper companyBasicMapper;

  @Override
  @Transactional
  public JobResponse createJob(JobRequest request) {
    log.info("Creating new job with title: {}", request.getTitle());

    User user = getCurrentUser();
    Company company = companyRepository.findByUserIdAndActiveTrue(user.getId())
        .orElseThrow(() -> new ResourceNotFoundException("Company not found for current user"));

    Job job = jobMapper.toEntity(request);
    job.setCompany(company);
    job.setPostedBy(user);

    processJobJsonFields(job, request);

    Job savedJob = jobRepository.save(job);
    log.info("Successfully created job with ID: {}", savedJob.getId());

    return toJobResponseWithApplicationCount(savedJob);
  }

  @Override
  @Transactional
  public JobResponse updateJob(Long id, JobRequest request) {
    log.info("Updating job with ID: {}", id);

    User user = getCurrentUser();
    Job job = findJobById(id);
    validateJobOwnership(job, user);

    jobMapper.updateJobFromDto(request, job);
    processJobJsonFields(job, request);

    Job updatedJob = jobRepository.save(job);
    log.info("Successfully updated job with ID: {}", id);

    return toJobResponseWithApplicationCount(updatedJob);
  }

  @Override
  @Transactional
  public void deleteJob(Long id) {
    log.info("Soft deleting job with ID: {}", id);

    User user = getCurrentUser();
    Job job = findJobById(id);
    validateJobOwnership(job, user);

    job.setActive(false);
    jobRepository.save(job);

    log.info("Successfully soft deleted job with ID: {}", id);
  }

  @Override
  public JobResponse getJob(Long id) {
    log.debug("Fetching job with ID: {}", id);
    Job job = findJobById(id);
    return toJobResponseWithApplicationCount(job);
  }

  @Override
  public Page<JobResponse> getAllJobs(int page, int size) {
    Pageable pageable = PageRequest.of(page, size);

    return jobRepository.findByActiveTrueOrderByCreatedAtDesc(pageable)
        .map(this::toJobResponseWithApplicationCount);
  }

  @Override
  @Transactional
  public JobResponse applyJob(Long id) {
    log.info("User applying for job with ID: {}", id);

    User user = getCurrentUser();
    Job job = findJobById(id);

    if (applicationRepository.existsByUserAndJob(user, job)) {
      throw new BadRequestException("You have already applied for this job");
    }

    Application application = new Application();
    application.setUser(user);
    application.setJob(job);
    application.setStatus(ApplicationStatus.PENDING);
    applicationRepository.save(application);

    log.info("User {} successfully applied for job {}", user.getId(), id);
    return toJobResponseWithApplicationCount(job);
  }

  @Override
  public Page<JobResponse> getAppliedJobs(int page, int size) {
    log.debug("Fetching applied jobs for user, page: {}, size: {}", page, size);

    User user = getCurrentUser();
    Pageable pageable = PageRequest.of(page, size);

    return jobRepository.findByApplicationsUser(user, pageable)
        .map(this::toJobResponseWithApplicationCount);
  }

  @Override
  public Page<JobResponse> getCompanyJobs(int page, int size, Long companyId) {
    Pageable pageable = PageRequest.of(page, size);

    return jobRepository.findByCompanyIdAndActiveTrue(companyId, pageable)
        .map(this::toJobResponseWithApplicationCount);
  }

  @Override
  public Page<JobResponse> searchJobs(int page, int size, String keyword, String location, String employmentType,
      String experienceLevel, Boolean isRemote, String city, String category, String skill,
      Double minSalary, Double maxSalary) {
    log.debug(
        "Searching jobs with keyword: {}, location: {}, employmentType: {}, experienceLevel: {}, isRemote: {}, city: {}, category: {}, skill: {}, minSalary: {}, maxSalary: {}",
        keyword, location, employmentType, experienceLevel, isRemote, city, category, skill, minSalary, maxSalary);

    Pageable pageable = PageRequest.of(page, size);

    // Convert Double to BigDecimal for database query
    java.math.BigDecimal minSalaryBD = minSalary != null ? java.math.BigDecimal.valueOf(minSalary) : null;
    java.math.BigDecimal maxSalaryBD = maxSalary != null ? java.math.BigDecimal.valueOf(maxSalary) : null;

    return jobRepository.findAll(
        JobSpecification.buildSearchSpecification(keyword, location, employmentType, experienceLevel, isRemote, city,
            category, skill, minSalaryBD, maxSalaryBD),
        pageable)
        .map(this::toJobResponseWithApplicationCount);
  }

  @Override
  @Transactional
  public JobResponse saveJob(Long id) {
    log.info("User saving job with ID: {}", id);

    User user = getCurrentUser();
    Job job = findJobById(id);

    if (savedJobRepository.existsByUserAndJob(user, job)) {
      throw new BadRequestException("You have already saved this job");
    }

    SavedJob savedJob = new SavedJob();
    savedJob.setUser(user);
    savedJob.setJob(job);
    savedJobRepository.save(savedJob);

    log.info("User {} successfully saved job {}", user.getId(), id);
    return toJobResponseWithApplicationCount(job);
  }

  @Override
  @Transactional
  public void unsaveJob(Long id) {
    log.info("User unsaving job with ID: {}", id);

    User user = getCurrentUser();
    Job job = findJobById(id);

    savedJobRepository.deleteByUserAndJob(user, job);

    log.info("User {} successfully unsaved job {}", user.getId(), id);
  }

  @Override
  public Page<JobResponse> getSavedJobs(int page, int size) {
    log.debug("Fetching saved jobs for user, page: {}, size: {}", page, size);

    User user = getCurrentUser();
    Pageable pageable = PageRequest.of(page, size);

    return savedJobRepository.findByUser(user, pageable)
        .map(savedJob -> toJobResponseWithApplicationCount(savedJob.getJob()));
  }

  // Utility methods
  private User getCurrentUser() {
    String currentUserId = SecurityContextHolder.getContext().getAuthentication().getName();
    return userRepository.findByKeycloakId(currentUserId)
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));
  }

  private Job findJobById(Long id) {
    return jobRepository.findByIdWithCompany(id)
        .orElseThrow(() -> new ResourceNotFoundException("Job not found"));
  }

  private void validateJobOwnership(Job job, User user) {
    if (!job.getCompany().getUser().getId().equals(user.getId())) {
      throw new BadRequestException("You don't have permission to access this job");
    }
  }

  private void processJobJsonFields(Job job, JobRequest request) {
    try {
      // Categories
      if (request.getCategories() != null) {
        if (!request.getCategories().isEmpty()) {
          job.setCategories(objectMapper.writeValueAsString(request.getCategories()));
          log.debug("Set categories: {}", request.getCategories());
        } else {
          job.setCategories(null);
        }
      }

      // Required Skills
      if (request.getRequiredSkills() != null) {
        if (!request.getRequiredSkills().isEmpty()) {
          job.setRequiredSkills(objectMapper.writeValueAsString(request.getRequiredSkills()));
          log.debug("Set required skills: {}", request.getRequiredSkills());
        } else {
          job.setRequiredSkills(null);
        }
      }

      // Required Languages
      if (request.getRequiredLanguages() != null) {
        if (!request.getRequiredLanguages().isEmpty()) {
          job.setRequiredLanguages(objectMapper.writeValueAsString(request.getRequiredLanguages()));
          log.debug("Set required languages: {}", request.getRequiredLanguages());
        } else {
          job.setRequiredLanguages(null);
        }
      }

      // Job Requirements
      if (request.getJobRequirements() != null) {
        if (!request.getJobRequirements().trim().isEmpty()) {
          // Validate JSON format
          objectMapper.readTree(request.getJobRequirements());
          job.setJobRequirements(request.getJobRequirements());
          log.debug("Set job requirements JSON");
        } else {
          job.setJobRequirements(null);
        }
      }
    } catch (Exception e) {
      log.error("Error processing job JSON fields: {}", e.getMessage());
      throw new BadRequestException("Error processing job data: " + e.getMessage());
    }
  }

  private JobResponse toJobResponseWithApplicationCount(Job job) {
    JobResponse response = jobMapper.toResponse(job);
    long applicationCount = applicationRepository.countByJobId(job.getId());
    response.setNumberOfApplications(applicationCount);

    // Set activeJobsCount for company if company exists
    if (response.getCompany() != null && job.getCompany() != null) {
      Long activeJobsCount = jobRepository.countActiveJobsByCompanyId(job.getCompany().getId());
      response.getCompany().setActiveJobsCount(activeJobsCount);
    }

    return response;
  }
}