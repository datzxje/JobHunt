package com.jobhunt.controller;

import com.jobhunt.payload.Response;
import com.jobhunt.service.SkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/skills")
@RequiredArgsConstructor
public class SkillController {

  private final SkillService skillService;

  @GetMapping
  public ResponseEntity<?> getAllActiveSkills() {
    return ResponseEntity.ok(Response.ofSucceeded(skillService.getAllActiveSkills()));
  }

  @GetMapping("/search")
  public ResponseEntity<?> searchSkills(@RequestParam String keyword) {
    return ResponseEntity.ok(Response.ofSucceeded(skillService.searchSkills(keyword)));
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getSkillById(@PathVariable Long id) {
    return ResponseEntity.ok(Response.ofSucceeded(skillService.getSkillById(id)));
  }
}