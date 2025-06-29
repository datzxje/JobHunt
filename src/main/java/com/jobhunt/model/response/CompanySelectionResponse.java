package com.jobhunt.model.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CompanySelectionResponse {
  private Long id;
  private String name;
  private String logoUrl;
  private String industryType;
}