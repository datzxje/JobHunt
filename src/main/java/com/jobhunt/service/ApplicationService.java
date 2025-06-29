package com.jobhunt.service;

import com.jobhunt.model.entity.Application;
import com.jobhunt.model.request.ApplicationRequest;
import com.jobhunt.model.request.ApplicationUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

public interface ApplicationService {
  // Create application methods
  Application apply(ApplicationRequest request, MultipartFile cv);

  Application createApplication(Long userId, ApplicationRequest request);

  Application createApplication(Long userId, Long jobId, String coverLetter, Double expectedSalary,
      String candidateProfile, MultipartFile cv);

  // Get application methods
  Application getApplication(Long id);

  Page<Application> getUserApplications(Pageable pageable);

  Page<Application> getJobApplications(Long jobId, Pageable pageable);

  Page<Application> getApplicationsByJobId(Long jobId, Pageable pageable);

  Page<Application> getApplicationsByUserId(Long userId, Pageable pageable);

  // Update application methods
  Application updateStatus(Long id, Application.ApplicationStatus status);

  Application updateApplicationStatus(Long applicationId, Application.ApplicationStatus status);

  Application updateApplicationStatus(Long applicationId, Application.ApplicationStatus status, String rejectionReason);

  Application updateApplication(Long id, ApplicationUpdateRequest request, MultipartFile cv);

  // Interview related methods
  Application scheduleInterview(Long applicationId, LocalDateTime interviewDate, String interviewLocation);

  Application addInterviewNotes(Long applicationId, String notes);

  // Other methods
  void withdrawApplication(Long id);
}