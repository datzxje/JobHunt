package com.jobhunt.model.response;

import lombok.Data;

import java.time.Instant;

@Data
public class LanguageResponse {
  private Long id;
  private String name;
  private String isoCode;
  private boolean active;
  private Instant createdAt;
  private Instant updatedAt;
}