package com.jobhunt.service;

import com.jobhunt.model.entity.ServiceJob;
import com.jobhunt.model.request.ServiceJobRequest;
import com.jobhunt.model.response.ServiceJobResponse;

import java.math.BigDecimal;
import java.util.List;

public interface ServiceJobService {
  ServiceJobResponse createServiceJob(ServiceJobRequest request, Long userId);

  ServiceJobResponse getServiceJobById(Long id);

  List<ServiceJobResponse> getAllServiceJobs();

  List<ServiceJobResponse> getServiceJobsByUser(Long userId);

  List<ServiceJobResponse> getServiceJobsByType(ServiceJob.ServiceType serviceType);

  List<ServiceJobResponse> getServiceJobsByStatus(ServiceJob.JobStatus status);

  List<ServiceJobResponse> getUrgentServiceJobs();

  List<ServiceJobResponse> getServiceJobsAssignedToUser(Long userId);

  ServiceJobResponse updateServiceJob(Long id, ServiceJobRequest request);

  ServiceJobResponse assignServiceJob(Long id, Long userId);

  ServiceJobResponse updateServiceJobStatus(Long id, ServiceJob.JobStatus status);

  void deleteServiceJob(Long id);

  List<ServiceJobResponse> searchServiceJobs(String location, BigDecimal minBudget, BigDecimal maxBudget,
      ServiceJob.ServiceType serviceType);
}