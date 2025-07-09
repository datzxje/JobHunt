package com.jobhunt.service;

import com.jobhunt.model.request.JobRequest;
import com.jobhunt.model.response.JobResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface JobService {
  JobResponse createJob(JobRequest request);

  JobResponse updateJob(Long id, JobRequest request);

  void deleteJob(Long id);

  JobResponse getJob(Long id);

  Page<JobResponse> getCompanyJobs(int page, int size, Long companyId);

  Page<JobResponse> searchJobs(int page, int size, String keyword, String location, String employmentType,
      String experienceLevel, Boolean isRemote, String city, String category, String skill,
      Double minSalary, Double maxSalary);

  Page<JobResponse> getAllJobs(int page, int size);

  JobResponse applyJob(Long id);

  Page<JobResponse> getAppliedJobs(int page, int size);

  JobResponse saveJob(Long id);

  void unsaveJob(Long id);

  Page<JobResponse> getSavedJobs(int page, int size);

  // Job assignment methods
  void assignJob(Long jobId, Long userId, Long companyId);

  void unassignJob(Long jobId, Long companyId);

  Map<String, Object> getJobAssignment(Long jobId, Long companyId);

  // Get jobs assigned to a specific user
  Page<JobResponse> getJobsAssignedToUser(Long userId, int page, int size);

  // Method to get expired jobs for company admin
  Page<JobResponse> getExpiredJobsByCompany(Long companyId, int page, int size);
}