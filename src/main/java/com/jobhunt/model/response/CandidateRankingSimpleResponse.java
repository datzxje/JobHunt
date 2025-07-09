package com.jobhunt.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CandidateRankingSimpleResponse {
  private Long candidateId;
  private String candidateName;
  private Double averageScore;
  private Integer rank;
}