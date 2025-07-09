package com.jobhunt.service;

import com.jobhunt.model.response.LanguageResponse;

import java.util.List;

public interface LanguageService {
  List<LanguageResponse> getAllActiveLanguages();

  List<LanguageResponse> searchLanguages(String keyword);

  LanguageResponse getLanguageById(Long id);
}