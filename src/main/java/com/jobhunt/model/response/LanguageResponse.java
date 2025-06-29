package com.jobhunt.model.response;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class LanguageResponse {
  private Long id;
  private String name;
  private String isoCode;
  private boolean active;
  private Instant createdAt;
  private Instant updatedAt;
}