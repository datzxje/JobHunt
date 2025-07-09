package com.jobhunt.service.impl;

import com.jobhunt.model.entity.Job;
import com.jobhunt.model.entity.Company;
import com.jobhunt.model.entity.CompanyMember;
import com.jobhunt.repository.JobRepository;
import com.jobhunt.repository.CompanyMemberRepository;
import com.jobhunt.service.EmailService;
import com.jobhunt.service.JobExpirationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobExpirationServiceImpl implements JobExpirationService {

  private final JobRepository jobRepository;
  private final CompanyMemberRepository companyMemberRepository;
  private final EmailService emailService;

  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

  @Override
  @Transactional(readOnly = true)
  public void sendExpirationReminders() {
    log.info("Starting job expiration reminder process");

    LocalDate today = LocalDate.now();
    LocalDate tomorrow = today.plusDays(1);

    List<Job> jobsExpiringTomorrow = jobRepository.findJobsExpiringTomorrow(tomorrow);

    log.info("Found {} jobs expiring tomorrow ({}) - sending reminders today", jobsExpiringTomorrow.size(), tomorrow);

    for (Job job : jobsExpiringTomorrow) {
      try {
        sendReminderEmailForJob(job);
      } catch (Exception e) {
        log.error("Failed to send reminder email for job ID: {}", job.getId(), e);
      }
    }

    log.info("Completed job expiration reminder process");
  }

  @Override
  @Transactional(readOnly = true)
  public void sendExpiredNotifications() {
    log.info("Starting expired notification process");

    LocalDate today = LocalDate.now();
    LocalDate yesterday = today.minusDays(1);

    List<Job> jobsExpiredYesterday = jobRepository.findJobsExpiredYesterday(yesterday);

    log.info("Found {} jobs expired yesterday ({}) - sending notifications", jobsExpiredYesterday.size(), yesterday);

    for (Job job : jobsExpiredYesterday) {
      try {
        sendExpiredNotificationForJob(job);
      } catch (Exception e) {
        log.error("Failed to send expired notification email for job ID: {}", job.getId(), e);
      }
    }

    log.info("Completed expired notification process");
  }

  @Override
  @Transactional
  public void markExpiredJobs() {
    log.info("Starting mark expired jobs process");

    LocalDate today = LocalDate.now();

    List<Job> expiredJobs = jobRepository.findExpiredJobs(today);

    log.info("Found {} expired jobs to mark (deadline before {})", expiredJobs.size(), today);

    for (Job job : expiredJobs) {
      try {
        job.setExpired(true);
        jobRepository.save(job);

        // Gửi email thông báo job đã hết hạn
        sendExpiredNotificationForJob(job);

        log.info("Marked job ID: {} as expired (deadline: {})", job.getId(),
            job.getApplicationDeadline().toLocalDate());
      } catch (Exception e) {
        log.error("Failed to mark job ID: {} as expired", job.getId(), e);
      }
    }

    log.info("Completed mark expired jobs process");
  }

  @Override
  public void processJobExpiration() {
    log.info("Starting complete job expiration process at midnight");

    // Gửi reminder cho jobs hết hạn NGÀY MAI (deadline = tomorrow)
    sendExpirationReminders();

    // Gửi notification cho jobs đã hết hạn HÔM QUA (deadline = yesterday)
    sendExpiredNotifications();

    // Đánh dấu expired cho jobs đã hết hạn (deadline < today)
    markExpiredJobs();

    log.info("Completed complete job expiration process at midnight");
  }

  @Override
  public void sendRemindersAndNotifications() {
    log.info("Starting send reminders and notifications process");

    // Gửi reminder cho jobs hết hạn NGÀY MAI
    sendExpirationReminders();

    // Gửi notification cho jobs đã hết hạn HÔM QUA
    sendExpiredNotifications();

    log.info("Completed send reminders and notifications process");
  }

  private void sendReminderEmailForJob(Job job) {
    Company company = job.getCompany();

    // Lấy danh sách admin của công ty
    Set<CompanyMember> companyMembers = companyMemberRepository.findByCompanyAndRole(company,
        CompanyMember.MemberRole.ADMIN);

    if (companyMembers.isEmpty()) {
      log.warn("No admin found for company ID: {} for job ID: {}", company.getId(), job.getId());
      return;
    }

    String expirationDate = job.getApplicationDeadline().format(DATE_FORMATTER);

    for (CompanyMember member : companyMembers) {
      try {
        emailService.sendJobExpirationReminderEmail(
            member.getUser().getEmail(),
            company.getName(),
            job.getTitle(),
            job.getId(),
            expirationDate);

        log.info("Sent expiration reminder email to admin: {} for job ID: {}",
            member.getUser().getEmail(), job.getId());
      } catch (Exception e) {
        log.error("Failed to send reminder email to admin: {} for job ID: {}",
            member.getUser().getEmail(), job.getId(), e);
      }
    }
  }

  private void sendExpiredNotificationForJob(Job job) {
    Company company = job.getCompany();

    // Lấy danh sách admin của công ty
    Set<CompanyMember> companyMembers = companyMemberRepository.findByCompanyAndRole(company,
        CompanyMember.MemberRole.ADMIN);

    if (companyMembers.isEmpty()) {
      log.warn("No admin found for company ID: {} for job ID: {}", company.getId(), job.getId());
      return;
    }

    for (CompanyMember member : companyMembers) {
      try {
        emailService.sendJobExpiredNotificationEmail(
            member.getUser().getEmail(),
            company.getName(),
            job.getTitle(),
            job.getId());

        log.info("Sent expired notification email to admin: {} for job ID: {}",
            member.getUser().getEmail(), job.getId());
      } catch (Exception e) {
        log.error("Failed to send expired notification email to admin: {} for job ID: {}",
            member.getUser().getEmail(), job.getId(), e);
      }
    }
  }
}