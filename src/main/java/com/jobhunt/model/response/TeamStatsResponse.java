package com.jobhunt.model.response;

import lombok.Data;

@Data
public class TeamStatsResponse {
  private long totalMembers;
  private long admins;
  private long hrMembers;
  private long activeMembers;
  private long inactiveMembers;
  private long pendingRequests;
}