package com.jobhunt.service;

import com.jobhunt.model.response.CandidateRankingSimpleResponse;
import java.util.List;

public interface CandidateRankingService {
  List<CandidateRankingSimpleResponse> rankCandidatesForJob(Long jobId);
}