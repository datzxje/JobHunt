package com.jobhunt.service.impl;

import com.jobhunt.exception.ResourceNotFoundException;
import com.jobhunt.mapper.JobCategoryMapper;
import com.jobhunt.model.response.JobCategoryResponse;
import com.jobhunt.repository.JobCategoryRepository;
import com.jobhunt.service.JobCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JobCategoryServiceImpl implements JobCategoryService {

  private final JobCategoryRepository jobCategoryRepository;
  private final JobCategoryMapper jobCategoryMapper;

  @Override
  public List<JobCategoryResponse> getAllActiveCategories() {
    return jobCategoryRepository.findByActiveTrue()
        .stream()
        .map(jobCategoryMapper::toResponse)
        .toList();
  }

  @Override
  public List<JobCategoryResponse> searchCategories(String keyword) {
    return jobCategoryRepository.findByKeyword(keyword)
        .stream()
        .map(jobCategoryMapper::toResponse)
        .toList();
  }

  @Override
  public JobCategoryResponse getCategoryById(Long id) {
    return jobCategoryRepository.findById(id)
        .map(jobCategoryMapper::toResponse)
        .orElseThrow(() -> new ResourceNotFoundException("Job category not found"));
  }
}