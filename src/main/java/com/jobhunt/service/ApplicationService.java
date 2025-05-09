package com.jobhunt.service;

import com.jobhunt.model.entity.Application;
import com.jobhunt.model.request.ApplicationRequest;
import com.jobhunt.model.request.ApplicationUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface ApplicationService {
  Application apply(ApplicationRequest request, MultipartFile cv);

  Application getApplication(Long id);

  Page<Application> getUserApplications(Pageable pageable);

  Page<Application> getJobApplications(Long jobId, Pageable pageable);

  Application updateStatus(Long id, Application.ApplicationStatus status);

  void withdrawApplication(Long id);

  Application updateApplication(Long id, ApplicationUpdateRequest request, MultipartFile cv);
}