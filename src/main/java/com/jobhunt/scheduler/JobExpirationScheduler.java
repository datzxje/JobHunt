package com.jobhunt.scheduler;

import com.jobhunt.service.JobExpirationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JobExpirationScheduler {

  private final JobExpirationService jobExpirationService;

  /**
   * Scheduled job to send reminders for jobs expiring today.
   * Runs daily at 0 AM.
   */
  @Scheduled(cron = "0 0 0 * * *")
  public void processJobExpirationMidnight() {
    log.info("Starting midnight job expiration check at {}", java.time.LocalDateTime.now());

    try {
      jobExpirationService.processJobExpiration();
      log.info("Successfully completed midnight job expiration check");
    } catch (Exception e) {
      log.error("Error occurred during midnight job expiration check", e);
    }
  }
}