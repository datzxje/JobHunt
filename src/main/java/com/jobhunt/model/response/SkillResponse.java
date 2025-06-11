package com.jobhunt.model.response;

import lombok.Data;

import java.time.Instant;

@Data
public class SkillResponse {
  private Long id;
  private String name;
  private String description;
  private boolean active;
  private Instant createdAt;
  private Instant updatedAt;
}