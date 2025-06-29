package com.jobhunt.service;

import com.jobhunt.model.request.JobRequest;
import com.jobhunt.model.response.JobResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

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
}