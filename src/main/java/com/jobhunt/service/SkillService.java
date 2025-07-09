package com.jobhunt.service;

import com.jobhunt.model.response.SkillResponse;

import java.util.List;

public interface SkillService {
  List<SkillResponse> getAllActiveSkills();

  List<SkillResponse> searchSkills(String keyword);

  SkillResponse getSkillById(Long id);
}