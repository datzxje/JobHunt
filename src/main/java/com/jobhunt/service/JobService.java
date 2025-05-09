package com.jobhunt.service;

import com.jobhunt.model.request.JobRequest;
import com.jobhunt.model.response.JobResponse;

import java.util.List;

public interface JobService {
  JobResponse createJob(JobRequest request);

  JobResponse updateJob(String id, JobRequest request);

  void deleteJob(String id);

  JobResponse getJob(String id);

  List<JobResponse> getCompanyJobs(String companyId);

  List<JobResponse> searchJobs(String keyword, String location, String employmentType, String experienceLevel,
      Boolean isRemote);
}