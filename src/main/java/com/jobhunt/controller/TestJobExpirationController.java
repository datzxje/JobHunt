package com.jobhunt.controller;

import com.jobhunt.service.JobExpirationService;
import com.jobhunt.payload.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/test/job-expiration")
@RequiredArgsConstructor
@Slf4j
public class TestJobExpirationController {

  private final JobExpirationService jobExpirationService;

  /**
   * Test endpoint để trigger job expiration process manually
   * Chỉ hoạt động khi app.test.controllers.enabled=true
   */
  @PostMapping("/trigger")
  public ResponseEntity<?> triggerJobExpiration() {
    log.info("=== MANUAL TEST: Triggering job expiration process ===");

    try {
      jobExpirationService.processJobExpiration();
      return ResponseEntity.ok(Response.ofSucceeded("Job expiration process completed successfully"));
    } catch (Exception e) {
      log.error("Error during manual job expiration test", e);
      return ResponseEntity.status(500).body(Response.ofSucceeded("Error: " + e.getMessage()));
    }
  }

  /**
   * Test endpoint để chỉ gửi reminder emails
   */
  @PostMapping("/send-reminders")
  public ResponseEntity<?> sendReminders() {
    log.info("=== MANUAL TEST: Sending expiration reminders ===");

    try {
      jobExpirationService.sendExpirationReminders();
      return ResponseEntity.ok(Response.ofSucceeded("Reminder emails sent successfully"));
    } catch (Exception e) {
      log.error("Error during manual reminder test", e);
      return ResponseEntity.status(500).body(Response.ofSucceeded("Error: " + e.getMessage()));
    }
  }

  /**
   * Test endpoint để chỉ mark expired jobs
   */
  @PostMapping("/mark-expired")
  public ResponseEntity<?> markExpiredJobs() {
    log.info("=== MANUAL TEST: Marking expired jobs ===");

    try {
      jobExpirationService.markExpiredJobs();
      return ResponseEntity.ok(Response.ofSucceeded("Expired jobs marked successfully"));
    } catch (Exception e) {
      log.error("Error during manual expired marking test", e);
      return ResponseEntity.status(500).body(Response.ofSucceeded("Error: " + e.getMessage()));
    }
  }

  /**
   * Test endpoint để gửi notification cho jobs đã expired hôm qua
   */
  @PostMapping("/send-expired-notifications")
  public ResponseEntity<?> sendExpiredNotifications() {
    log.info("=== MANUAL TEST: Sending expired notifications ===");

    try {
      jobExpirationService.sendExpiredNotifications();
      return ResponseEntity.ok(Response.ofSucceeded("Expired notifications sent successfully"));
    } catch (Exception e) {
      log.error("Error during manual expired notification test", e);
      return ResponseEntity.status(500).body(Response.ofSucceeded("Error: " + e.getMessage()));
    }
  }

  /**
   * Test endpoint để gửi cả reminder và notification cùng lúc
   */
  @PostMapping("/send-reminders-and-notifications")
  public ResponseEntity<?> sendRemindersAndNotifications() {
    log.info("=== MANUAL TEST: Sending reminders and notifications ===");

    try {
      jobExpirationService.sendRemindersAndNotifications();
      return ResponseEntity.ok(Response.ofSucceeded("Reminders and notifications sent successfully"));
    } catch (Exception e) {
      log.error("Error during manual reminders and notifications test", e);
      return ResponseEntity.status(500).body(Response.ofSucceeded("Error: " + e.getMessage()));
    }
  }
}