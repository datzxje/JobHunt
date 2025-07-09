package com.jobhunt.service;

import com.jobhunt.model.response.JobCategoryResponse;

import java.util.List;

public interface JobCategoryService {
  List<JobCategoryResponse> getAllActiveCategories();

  List<JobCategoryResponse> searchCategories(String keyword);

  JobCategoryResponse getCategoryById(Long id);
}