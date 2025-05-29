package com.jobhunt.service.impl;

import com.jobhunt.exception.BadRequestException;
import com.jobhunt.exception.ResourceNotFoundException;
import com.jobhunt.mapper.JobMapper;
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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JobServiceImpl implements JobService {

  private final JobRepository jobRepository;
  private final UserRepository userRepository;
  private final CompanyRepository companyRepository;
  private final ApplicationRepository applicationRepository;
  private final JobMapper jobMapper;
  private final SavedJobRepository savedJobRepository;

  @Override
  @Transactional
  public JobResponse createJob(JobRequest request) {
    String currentUserId = SecurityContextHolder.getContext().getAuthentication().getName();

    var user = userRepository.findByKeycloakId(currentUserId)
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    Company company = companyRepository.findByUserIdAndActiveTrue(user.getId())
        .orElseThrow(() -> new ResourceNotFoundException("Company not found for current user"));

    Job job = jobMapper.toEntity(request);
    job.setCompany(company);

    return jobMapper.toResponse(jobRepository.save(job));
  }

  @Override
  @Transactional
  public JobResponse updateJob(Long id, JobRequest request) {
    String currentUserId = SecurityContextHolder.getContext().getAuthentication().getName();

    Job job = jobRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Job not found"));

    var user = userRepository.findByKeycloakId(currentUserId)
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    if (!job.getCompany().getUser().getId().equals(user.getId())) {
      throw new BadRequestException("You don't have permission to update this job");
    }

    jobMapper.updateJobFromDto(request, job);
    return jobMapper.toResponse(jobRepository.save(job));
  }

  @Override
  @Transactional
  public void deleteJob(Long id) {
    String currentUserId = SecurityContextHolder.getContext().getAuthentication().getName();

    Job job = jobRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Job not found"));

    var user = userRepository.findByKeycloakId(currentUserId)
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    if (!job.getCompany().getUser().getId().equals(user.getId())) {
      throw new BadRequestException("You don't have permission to delete this job");
    }

    job.setActive(false);
    jobRepository.save(job);
  }

  @Override
  public JobResponse getJob(Long id) {
    return jobRepository.findById(id)
        .map(jobMapper::toResponse)
        .orElseThrow(() -> new ResourceNotFoundException("Job not found"));
  }

  @Override
  public Page<JobResponse> getAllJobs(int page, int size, String keyword, String location, String jobType,
      String experienceLevel, String salaryRange) {
    Pageable pageable = PageRequest.of(page, size);
    return jobRepository.findAll(pageable).map(jobMapper::toResponse);
  }

  @Override
  @Transactional
  public JobResponse applyJob(Long id) {
    String currentUserId = SecurityContextHolder.getContext().getAuthentication().getName();

    User user = userRepository.findByKeycloakId(currentUserId)
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    Job job = jobRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Job not found"));

    if (applicationRepository.existsByUserAndJob(user, job)) {
      throw new BadRequestException("You have already applied for this job");
    }

    Application application = new Application();
    application.setUser(user);
    application.setJob(job);
    application.setStatus(ApplicationStatus.PENDING);
    applicationRepository.save(application);

    return jobMapper.toResponse(job);
  }

  @Override
  public Page<JobResponse> getAppliedJobs(int page, int size) {
    String currentUserId = SecurityContextHolder.getContext().getAuthentication().getName();

    User user = userRepository.findByKeycloakId(currentUserId)
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    Pageable pageable = PageRequest.of(page, size);
    return jobRepository.findByApplicationsUser(user, pageable)
        .map(jobMapper::toResponse);
  }

  @Override
  public List<JobResponse> getCompanyJobs(Long companyId) {
    return jobRepository.findByCompanyIdAndActiveTrue(companyId)
        .stream()
        .map(jobMapper::toResponse)
        .toList();
  }

  @Override
  public List<JobResponse> searchJobs(String keyword, String location, String employmentType,
      String experienceLevel, Boolean isRemote) {
    return jobRepository.searchJobs(keyword, location, employmentType, experienceLevel, isRemote)
        .stream()
        .map(jobMapper::toResponse)
        .toList();
  }

  @Override
  @Transactional
  public JobResponse saveJob(Long id) {
    String currentUserId = SecurityContextHolder.getContext().getAuthentication().getName();

    User user = userRepository.findByKeycloakId(currentUserId)
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    Job job = jobRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Job not found"));

    if (savedJobRepository.existsByUserAndJob(user, job)) {
      throw new BadRequestException("You have already saved this job");
    }

    SavedJob savedJob = new SavedJob();
    savedJob.setUser(user);
    savedJob.setJob(job);
    savedJobRepository.save(savedJob);

    return jobMapper.toResponse(job);
  }

  @Override
  @Transactional
  public void unsaveJob(Long id) {
    String currentUserId = SecurityContextHolder.getContext().getAuthentication().getName();

    User user = userRepository.findByKeycloakId(currentUserId)
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    Job job = jobRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Job not found"));

    savedJobRepository.deleteByUserAndJob(user, job);
  }

  @Override
  public Page<JobResponse> getSavedJobs(int page, int size) {
    String currentUserId = SecurityContextHolder.getContext().getAuthentication().getName();

    User user = userRepository.findByKeycloakId(currentUserId)
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    Pageable pageable = PageRequest.of(page, size);
    return savedJobRepository.findByUser(user, pageable)
        .map(savedJob -> jobMapper.toResponse(savedJob.getJob()));
  }
}