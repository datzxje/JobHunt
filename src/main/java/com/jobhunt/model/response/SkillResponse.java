package com.jobhunt.model.response;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class SkillResponse {
  private Long id;
  private String name;
  private String description;
  private boolean active;
  private Instant createdAt;
  private Instant updatedAt;
}