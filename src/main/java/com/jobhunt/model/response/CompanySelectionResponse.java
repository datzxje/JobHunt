package com.jobhunt.model.response;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompanySelectionResponse {
  private Long id;
  private String name;
  private String logoUrl;
  private String industryType;
}