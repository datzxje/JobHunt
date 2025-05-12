package com.jobhunt.service.impl;

import com.jobhunt.exception.ResourceNotFoundException;
import com.jobhunt.mapper.ServiceJobMapper;
import com.jobhunt.model.entity.ServiceJob;
import com.jobhunt.model.entity.User;
import com.jobhunt.model.request.ServiceJobRequest;
import com.jobhunt.model.response.ServiceJobResponse;
import com.jobhunt.repository.ServiceJobRepository;
import com.jobhunt.repository.UserRepository;
import com.jobhunt.service.ServiceJobService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServiceJobServiceImpl implements ServiceJobService {

  private final ServiceJobRepository serviceJobRepository;
  private final UserRepository userRepository;
  private final ServiceJobMapper serviceJobMapper;

  @Override
  @Transactional
  public ServiceJobResponse createServiceJob(ServiceJobRequest request, Long userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

    ServiceJob serviceJob = serviceJobMapper.toEntity(request);
    serviceJob.setPostedByUser(user);

    ServiceJob savedServiceJob = serviceJobRepository.save(serviceJob);
    return serviceJobMapper.toResponse(savedServiceJob);
  }

  @Override
  public ServiceJobResponse getServiceJobById(Long id) {
    ServiceJob serviceJob = serviceJobRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Service job not found with id: " + id));
    return serviceJobMapper.toResponse(serviceJob);
  }

  @Override
  public List<ServiceJobResponse> getAllServiceJobs() {
    return serviceJobRepository.findByActiveTrue().stream()
        .map(serviceJobMapper::toResponse)
        .collect(Collectors.toList());
  }

  @Override
  public List<ServiceJobResponse> getServiceJobsByUser(Long userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

    return serviceJobRepository.findByPostedByUser(user).stream()
        .map(serviceJobMapper::toResponse)
        .collect(Collectors.toList());
  }

  @Override
  public List<ServiceJobResponse> getServiceJobsByType(ServiceJob.ServiceType serviceType) {
    return serviceJobRepository.findByServiceType(serviceType).stream()
        .map(serviceJobMapper::toResponse)
        .collect(Collectors.toList());
  }

  @Override
  public List<ServiceJobResponse> getServiceJobsByStatus(ServiceJob.JobStatus status) {
    return serviceJobRepository.findByStatus(status).stream()
        .map(serviceJobMapper::toResponse)
        .collect(Collectors.toList());
  }

  @Override
  public List<ServiceJobResponse> getUrgentServiceJobs() {
    return serviceJobRepository.findByIsUrgent(true).stream()
        .map(serviceJobMapper::toResponse)
        .collect(Collectors.toList());
  }

  @Override
  public List<ServiceJobResponse> getServiceJobsAssignedToUser(Long userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

    return serviceJobRepository.findByAssignedToUser(user).stream()
        .map(serviceJobMapper::toResponse)
        .collect(Collectors.toList());
  }

  @Override
  @Transactional
  public ServiceJobResponse updateServiceJob(Long id, ServiceJobRequest request) {
    ServiceJob serviceJob = serviceJobRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Service job not found with id: " + id));

    serviceJobMapper.updateEntity(serviceJob, request);
    ServiceJob updatedServiceJob = serviceJobRepository.save(serviceJob);
    return serviceJobMapper.toResponse(updatedServiceJob);
  }

  @Override
  @Transactional
  public ServiceJobResponse assignServiceJob(Long id, Long userId) {
    ServiceJob serviceJob = serviceJobRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Service job not found with id: " + id));

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

    serviceJob.setAssignedToUser(user);
    serviceJob.setStatus(ServiceJob.JobStatus.ASSIGNED);

    ServiceJob updatedServiceJob = serviceJobRepository.save(serviceJob);
    return serviceJobMapper.toResponse(updatedServiceJob);
  }

  @Override
  @Transactional
  public ServiceJobResponse updateServiceJobStatus(Long id, ServiceJob.JobStatus status) {
    ServiceJob serviceJob = serviceJobRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Service job not found with id: " + id));

    serviceJob.setStatus(status);

    ServiceJob updatedServiceJob = serviceJobRepository.save(serviceJob);
    return serviceJobMapper.toResponse(updatedServiceJob);
  }

  @Override
  @Transactional
  public void deleteServiceJob(Long id) {
    ServiceJob serviceJob = serviceJobRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Service job not found with id: " + id));

    // Soft delete by setting active to false
    serviceJob.setActive(false);
    serviceJobRepository.save(serviceJob);
  }

  @Override
  public List<ServiceJobResponse> searchServiceJobs(String location, BigDecimal minBudget, BigDecimal maxBudget,
      ServiceJob.ServiceType serviceType) {
    return serviceJobRepository.searchServiceJobs(location, minBudget, maxBudget, serviceType).stream()
        .map(serviceJobMapper::toResponse)
        .collect(Collectors.toList());
  }
}