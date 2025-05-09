package com.jobhunt.service.impl;

import com.jobhunt.exception.ResourceNotFoundException;
import com.jobhunt.mapper.JobMapper;
import com.jobhunt.model.entity.Job;
import com.jobhunt.model.request.JobRequest;
import com.jobhunt.model.response.JobResponse;
import com.jobhunt.repository.JobRepository;
import com.jobhunt.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JobServiceImpl implements JobService {

  private final JobRepository jobRepository;
  private final JobMapper jobMapper;

  @Override
  @Transactional
  public JobResponse createJob(JobRequest request) {
    String currentUserId = SecurityContextHolder.getContext().getAuthentication().getName();
    Job job = jobMapper.toEntity(request);
    job.setActive(true);
    // TODO: Set company based on current user
    return jobMapper.toResponse(jobRepository.save(job));
  }

  @Override
  @Transactional
  public JobResponse updateJob(String id, JobRequest request) {
    Job job = jobRepository.findById(Long.parseLong(id))
        .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + id));

    // TODO: Check if current user belongs to the company that posted this job
    jobMapper.updateJobFromDto(request, job);
    return jobMapper.toResponse(jobRepository.save(job));
  }

  @Override
  @Transactional
  public void deleteJob(String id) {
    Job job = jobRepository.findById(Long.parseLong(id))
        .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + id));

    // TODO: Check if current user belongs to the company that posted this job
    job.setActive(false);
    jobRepository.save(job);
  }

  @Override
  public JobResponse getJob(String id) {
    return jobRepository.findById(Long.parseLong(id))
        .map(jobMapper::toResponse)
        .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + id));
  }

  @Override
  public List<JobResponse> getCompanyJobs(String companyId) {
    return jobRepository.findByCompanyIdAndActiveTrue(Long.parseLong(companyId))
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
}