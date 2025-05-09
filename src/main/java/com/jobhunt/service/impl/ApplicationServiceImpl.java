package com.jobhunt.service.impl;

import com.jobhunt.exception.BadRequestException;
import com.jobhunt.exception.ResourceNotFoundException;
import com.jobhunt.model.entity.Application;
import com.jobhunt.model.entity.Job;
import com.jobhunt.model.entity.User;
import com.jobhunt.model.request.ApplicationRequest;
import com.jobhunt.model.request.ApplicationUpdateRequest;
import com.jobhunt.repository.ApplicationRepository;
import com.jobhunt.repository.JobRepository;
import com.jobhunt.repository.UserRepository;
import com.jobhunt.service.ApplicationService;
import com.jobhunt.service.FileStorageService;
import com.jobhunt.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {

  private final ApplicationRepository applicationRepository;
  private final JobRepository jobRepository;
  private final UserRepository userRepository;
  private final FileStorageService fileStorageService;

  @Override
  @Transactional
  public Application apply(ApplicationRequest request, MultipartFile cv) {
    User user = userRepository.findByEmail(SecurityUtil.getCurrentUserLogin()
        .orElseThrow(() -> new BadRequestException("User not found")))
        .orElseThrow(() -> new BadRequestException("User not found"));

    Job job = jobRepository.findById(request.getJobId())
        .orElseThrow(() -> new ResourceNotFoundException("Job not found"));

    // Check if user has already applied
    if (applicationRepository.existsByUserAndJob(user, job)) {
      throw new BadRequestException("You have already applied for this job");
    }

    // Upload CV to S3
    String cvUrl = fileStorageService.uploadFile(cv, "cv");

    Application application = new Application();
    application.setUser(user);
    application.setJob(job);
    application.setCvUrl(cvUrl);
    application.setCoverLetter(request.getCoverLetter());
    application.setExpectedSalary(request.getExpectedSalary());
    application.setStatus(Application.ApplicationStatus.PENDING);

    return applicationRepository.save(application);
  }

  @Override
  public Application getApplication(Long id) {
    return applicationRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Application not found"));
  }

  @Override
  public Page<Application> getUserApplications(Pageable pageable) {
    User user = userRepository.findByEmail(SecurityUtil.getCurrentUserLogin()
        .orElseThrow(() -> new BadRequestException("User not found")))
        .orElseThrow(() -> new BadRequestException("User not found"));
    return applicationRepository.findByUser(user, pageable);
  }

  @Override
  public Page<Application> getJobApplications(Long jobId, Pageable pageable) {
    Job job = jobRepository.findById(jobId)
        .orElseThrow(() -> new ResourceNotFoundException("Job not found"));
    return applicationRepository.findByJob(job, pageable);
  }

  @Override
  @Transactional
  public Application updateStatus(Long id, Application.ApplicationStatus status) {
    Application application = getApplication(id);
    application.setStatus(status);
    return applicationRepository.save(application);
  }

  @Override
  @Transactional
  public void withdrawApplication(Long id) {
    Application application = getApplication(id);

    // Check if the current user is the owner of the application
    User currentUser = userRepository.findByEmail(SecurityUtil.getCurrentUserLogin()
        .orElseThrow(() -> new BadRequestException("User not found")))
        .orElseThrow(() -> new BadRequestException("User not found"));

    if (!application.getUser().getId().equals(currentUser.getId())) {
      throw new BadRequestException("You can only withdraw your own applications");
    }

    // Delete CV from S3
    if (application.getCvUrl() != null) {
      fileStorageService.deleteFile(application.getCvUrl());
    }

    applicationRepository.delete(application);
  }

  @Override
  @Transactional
  public Application updateApplication(Long id, ApplicationUpdateRequest request, MultipartFile cv) {
    Application application = getApplication(id);

    // Check if the current user is the owner of the application
    User currentUser = userRepository.findByEmail(SecurityUtil.getCurrentUserLogin()
        .orElseThrow(() -> new BadRequestException("User not found")))
        .orElseThrow(() -> new BadRequestException("User not found"));

    if (!application.getUser().getId().equals(currentUser.getId())) {
      throw new BadRequestException("You can only update your own applications");
    }

    // Check if application is in an editable state
    if (application.getStatus() != Application.ApplicationStatus.PENDING &&
        application.getStatus() != Application.ApplicationStatus.REVIEWING) {
      throw new BadRequestException("Application cannot be edited in its current status");
    }

    // Update CV if provided
    if (cv != null && !cv.isEmpty()) {
      // Delete old CV from S3
      if (application.getCvUrl() != null) {
        fileStorageService.deleteFile(application.getCvUrl());
      }
      // Upload new CV
      String cvUrl = fileStorageService.uploadFile(cv, "cv");
      application.setCvUrl(cvUrl);
    }

    // Update other fields
    application.setCoverLetter(request.getCoverLetter());
    application.setExpectedSalary(request.getExpectedSalary());

    return applicationRepository.save(application);
  }
}