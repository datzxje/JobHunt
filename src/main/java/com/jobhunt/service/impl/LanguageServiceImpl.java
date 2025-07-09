package com.jobhunt.service.impl;

import com.jobhunt.exception.ResourceNotFoundException;
import com.jobhunt.mapper.LanguageMapper;
import com.jobhunt.model.response.LanguageResponse;
import com.jobhunt.repository.LanguageRepository;
import com.jobhunt.service.LanguageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LanguageServiceImpl implements LanguageService {

  private final LanguageRepository languageRepository;
  private final LanguageMapper languageMapper;

  @Override
  public List<LanguageResponse> getAllActiveLanguages() {
    return languageRepository.findByActiveTrue()
        .stream()
        .map(languageMapper::toResponse)
        .toList();
  }

  @Override
  public List<LanguageResponse> searchLanguages(String keyword) {
    return languageRepository.findByKeyword(keyword)
        .stream()
        .map(languageMapper::toResponse)
        .toList();
  }

  @Override
  public LanguageResponse getLanguageById(Long id) {
    return languageRepository.findById(id)
        .map(languageMapper::toResponse)
        .orElseThrow(() -> new ResourceNotFoundException("Language not found"));
  }
}