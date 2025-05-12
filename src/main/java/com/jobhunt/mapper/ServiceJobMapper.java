package com.jobhunt.mapper;

import com.jobhunt.model.entity.ServiceJob;
import com.jobhunt.model.entity.User;
import com.jobhunt.model.request.ServiceJobRequest;
import com.jobhunt.model.response.ServiceJobResponse;

import org.springframework.stereotype.Component;

@Component
public class ServiceJobMapper {

  public ServiceJob toEntity(ServiceJobRequest request) {
    ServiceJob serviceJob = new ServiceJob();
    serviceJob.setTitle(request.getTitle());
    serviceJob.setDescription(request.getDescription());
    serviceJob.setServiceType(request.getServiceType());
    serviceJob.setLocation(request.getLocation());
    serviceJob.setEstimatedBudget(request.getEstimatedBudget());
    serviceJob.setRequiredCompletionDate(request.getRequiredCompletionDate());
    serviceJob.setUrgent(request.isUrgent());
    serviceJob.setActive(request.isActive());
    serviceJob.setStatus(ServiceJob.JobStatus.OPEN);
    return serviceJob;
  }

  public void updateEntity(ServiceJob serviceJob, ServiceJobRequest request) {
    serviceJob.setTitle(request.getTitle());
    serviceJob.setDescription(request.getDescription());
    serviceJob.setServiceType(request.getServiceType());
    serviceJob.setLocation(request.getLocation());
    serviceJob.setEstimatedBudget(request.getEstimatedBudget());
    serviceJob.setRequiredCompletionDate(request.getRequiredCompletionDate());
    serviceJob.setUrgent(request.isUrgent());
    serviceJob.setActive(request.isActive());
  }

  public ServiceJobResponse toResponse(ServiceJob serviceJob) {
    ServiceJobResponse response = new ServiceJobResponse();
    response.setId(serviceJob.getId());
    response.setTitle(serviceJob.getTitle());
    response.setDescription(serviceJob.getDescription());
    response.setServiceType(serviceJob.getServiceType());
    response.setLocation(serviceJob.getLocation());
    response.setEstimatedBudget(serviceJob.getEstimatedBudget());
    response.setRequiredCompletionDate(serviceJob.getRequiredCompletionDate());
    response.setUrgent(serviceJob.isUrgent());
    response.setActive(serviceJob.isActive());

    User postedByUser = serviceJob.getPostedByUser();
    if (postedByUser != null) {
      response.setPostedByUserId(postedByUser.getId());
      response.setPostedByUserName(postedByUser.getFirstName() + " " + postedByUser.getLastName());
    }

    User assignedToUser = serviceJob.getAssignedToUser();
    if (assignedToUser != null) {
      response.setAssignedToUserId(assignedToUser.getId());
      response.setAssignedToUserName(assignedToUser.getFirstName() + " " + assignedToUser.getLastName());
    }

    response.setStatus(serviceJob.getStatus());
    response.setApplicationsCount(serviceJob.getApplications().size());
    response.setCreatedAt(serviceJob.getCreatedAt());
    response.setUpdatedAt(serviceJob.getUpdatedAt());

    return response;
  }
}