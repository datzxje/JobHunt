package com.jobhunt.service.impl;

import com.jobhunt.exception.ResourceNotFoundException;
import com.jobhunt.mapper.SkillMapper;
import com.jobhunt.model.response.SkillResponse;
import com.jobhunt.repository.SkillRepository;
import com.jobhunt.service.SkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillServiceImpl implements SkillService {

  private final SkillRepository skillRepository;
  private final SkillMapper skillMapper;

  @Override
  public List<SkillResponse> getAllActiveSkills() {
    return skillRepository.findByActiveTrue()
        .stream()
        .map(skillMapper::toResponse)
        .toList();
  }

  @Override
  public List<SkillResponse> searchSkills(String keyword) {
    return skillRepository.findByKeyword(keyword)
        .stream()
        .map(skillMapper::toResponse)
        .toList();
  }

  @Override
  public SkillResponse getSkillById(Long id) {
    return skillRepository.findById(id)
        .map(skillMapper::toResponse)
        .orElseThrow(() -> new ResourceNotFoundException("Skill not found"));
  }
}