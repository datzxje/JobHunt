package com.jobhunt.controller;

import com.jobhunt.model.response.CandidateRankingSimpleResponse;
import com.jobhunt.payload.Response;
import com.jobhunt.service.CandidateRankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/jobs")
@RequiredArgsConstructor
public class CandidateRankingController {

  private final CandidateRankingService candidateRankingService;

  @GetMapping("/{jobId}/applications/ranked")
  @PreAuthorize("hasRole('EMPLOYER')")
  public ResponseEntity<?> getRankedCandidates(@PathVariable Long jobId) {
    List<CandidateRankingSimpleResponse> rankings = candidateRankingService.rankCandidatesForJob(jobId);
    return ResponseEntity.ok(Response.ofSucceeded(rankings));
  }
}