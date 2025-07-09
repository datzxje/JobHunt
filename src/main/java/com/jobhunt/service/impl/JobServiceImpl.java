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
import com.jobhunt.service.AuthService;
import com.jobhunt.service.JobService;
import com.jobhunt.specification.JobSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

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
  private final AuthService authService;

  @Override
  @Transactional
  public JobResponse createJob(JobRequest request) {
    log.info("Creating new job with title: {} for company: {}", request.getTitle(), request.getCompanyId());

    // Get company by ID from request
    Company company = companyRepository.findById(request.getCompanyId())
        .orElseThrow(() -> new ResourceNotFoundException("Company not found with ID: " + request.getCompanyId()));

    // Try to get current user, fallback to company user if SecurityContext is not
    // available
    User user = getCurrentUser();
    if (user == null) {
      user = company.getUser(); // Use company's user as fallback
      log.warn("SecurityContext not available, using company user as fallback");
    }

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
    Job job = jobRepository.findByIdWithCompanyAndActiveTrueAndExpiredFalse(id)
        .orElseThrow(() -> new ResourceNotFoundException("Job not found with ID: " + id));
    return toJobResponseWithApplicationCount(job);
  }

  @Override
  public Page<JobResponse> getAllJobs(int page, int size) {
    Pageable pageable = PageRequest.of(page, size);

    return jobRepository.findByActiveTrueAndExpiredFalseOrderByCreatedAtDesc(pageable)
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

    return jobRepository.findByApplicationsUserAndActiveTrueAndExpiredFalse(user, pageable)
        .map(this::toJobResponseWithApplicationCount);
  }

  @Override
  public Page<JobResponse> getCompanyJobs(int page, int size, Long companyId) {
    Pageable pageable = PageRequest.of(page, size);

    return jobRepository.findByCompanyIdAndActiveTrueAndExpiredFalse(companyId, pageable)
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

    return savedJobRepository.findByUserWithActiveAndNotExpiredJobs(user, pageable)
        .map(savedJob -> toJobResponseWithApplicationCount(savedJob.getJob()));
  }

  // Utility methods
  private User getCurrentUser() {
    log.debug("=== getCurrentUser() called ===");

    // Primary: Try SecurityContext first (set by AuthService during login/refresh)
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    log.debug("SecurityContext authentication: {} (authenticated: {})",
        authentication != null ? authentication.getClass().getSimpleName() : null,
        authentication != null ? authentication.isAuthenticated() : false);

    if (authentication != null && authentication.isAuthenticated()) {
      Object principal = authentication.getPrincipal();
      log.debug("Principal type: {}, authorities: {}",
          principal != null ? principal.getClass().getSimpleName() : null,
          authentication.getAuthorities());

      // Case 1: Principal is email (set by our AuthService)
      if (principal instanceof String email) {
        log.debug("Principal is email: {}", email);
        User user = userRepository.findByEmail(email).orElse(null);
        if (user != null) {
          log.info("Successfully got user from SecurityContext (email): {} with role: {}",
              user.getEmail(), user.getRole());
          return user;
        }
      }

      // Case 2: Principal is JWT (set by Spring Security JWT processing)
      if (principal instanceof Jwt jwt) {
        String keycloakId = jwt.getSubject();
        log.debug("Getting user ID for Keycloak ID from SecurityContext: {}", keycloakId);

        User user = userRepository.findByKeycloakId(keycloakId).orElse(null);
        if (user != null) {
          log.info("Successfully got user from SecurityContext (JWT): {} with role: {}",
              user.getEmail(), user.getRole());
          return user;
        }
      }
    }

    // Fallback: AuthService cookie-based authentication
    log.debug("Trying AuthService fallback...");
    User user = authService.getCurrentUserEntity();
    if (user != null) {
      log.info("Successfully got current user from AuthService: {} with role: {}",
          user.getEmail(), user.getRole());
      return user;
    }

    // Development fallback - hardcode user ID for testing
    log.warn("No authentication found, using fallback user for development");
    return userRepository.findById(1L).orElse(null); // Replace 1L with actual user ID
  }

  private Job findJobById(Long id) {
    return jobRepository.findByIdWithCompanyAndActiveTrueAndExpiredFalse(id)
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

  // Job assignment methods
  @Override
  @Transactional
  public void assignJob(Long jobId, Long userId, Long companyId) {
    log.info("Assigning job {} to user {} in company {}", jobId, userId, companyId);

    Job job = findJobById(jobId);

    // Validate that job belongs to the company
    if (!job.getCompany().getId().equals(companyId)) {
      throw new BadRequestException("Job does not belong to the specified company");
    }

    // Get the user to assign
    User assigneeUser = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    job.setAssignedTo(assigneeUser);
    jobRepository.save(job);

    log.info("Successfully assigned job {} to user {}", jobId, userId);
  }

  @Override
  @Transactional
  public void unassignJob(Long jobId, Long companyId) {
    log.info("Unassigning job {} in company {}", jobId, companyId);

    Job job = findJobById(jobId);

    // Validate that job belongs to the company
    if (!job.getCompany().getId().equals(companyId)) {
      throw new BadRequestException("Job does not belong to the specified company");
    }

    job.setAssignedTo(null);
    jobRepository.save(job);

    log.info("Successfully unassigned job {}", jobId);
  }

  @Override
  public Map<String, Object> getJobAssignment(Long jobId, Long companyId) {
    log.debug("Getting assignment for job {} in company {}", jobId, companyId);

    Job job = findJobById(jobId);

    // Validate that job belongs to the company
    if (!job.getCompany().getId().equals(companyId)) {
      throw new BadRequestException("Job does not belong to the specified company");
    }

    Map<String, Object> assignment = new HashMap<>();
    assignment.put("jobId", jobId);
    assignment.put("companyId", companyId);

    if (job.getAssignedTo() != null) {
      User assignedUser = job.getAssignedTo();
      Map<String, Object> assignedUserInfo = new HashMap<>();
      assignedUserInfo.put("id", assignedUser.getId());
      assignedUserInfo.put("email", assignedUser.getEmail());
      assignedUserInfo.put("firstName", assignedUser.getFirstName());
      assignedUserInfo.put("lastName", assignedUser.getLastName());
      assignedUserInfo.put("profilePicture", assignedUser.getProfilePictureUrl());

      assignment.put("assignedTo", assignedUserInfo);
      assignment.put("isAssigned", true);
    } else {
      assignment.put("assignedTo", null);
      assignment.put("isAssigned", false);
    }

    return assignment;
  }

  @Override
  public Page<JobResponse> getJobsAssignedToUser(Long userId, int page, int size) {
    log.debug("Getting jobs assigned to user: {}", userId);

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

    Pageable pageable = PageRequest.of(page, size);
    Page<Job> jobsPage = jobRepository.findByAssignedToAndActiveTrueAndExpiredFalse(user, pageable);

    log.debug("Found {} jobs assigned to user {}", jobsPage.getTotalElements(), userId);

    return jobsPage.map(jobMapper::toResponse);
  }

  @Override
  public Page<JobResponse> getExpiredJobsByCompany(Long companyId, int page, int size) {
    log.debug("Getting expired jobs for company: {}", companyId);

    Pageable pageable = PageRequest.of(page, size);
    Page<Job> expiredJobs = jobRepository.findByCompanyIdAndActiveTrueAndExpiredTrue(companyId, pageable);

    log.debug("Found {} expired jobs for company {}", expiredJobs.getTotalElements(), companyId);

    return expiredJobs.map(this::toJobResponseWithApplicationCount);
  }
}