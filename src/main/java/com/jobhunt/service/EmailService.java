package com.jobhunt.service;

public interface EmailService {
  /**
   * Gửi email nhắc nhở job sắp hết hạn cho admin công ty
   * 
   * @param recipientEmail Email của admin công ty
   * @param companyName    Tên công ty
   * @param jobTitle       Tiêu đề công việc
   * @param jobId          ID công việc
   * @param expirationDate Ngày hết hạn
   */
  void sendJobExpirationReminderEmail(String recipientEmail, String companyName, String jobTitle, Long jobId,
      String expirationDate);

  /**
   * Gửi email thông báo job đã hết hạn
   * 
   * @param recipientEmail Email của admin công ty
   * @param companyName    Tên công ty
   * @param jobTitle       Tiêu đề công việc
   * @param jobId          ID công việc
   */
  void sendJobExpiredNotificationEmail(String recipientEmail, String companyName, String jobTitle, Long jobId);
}