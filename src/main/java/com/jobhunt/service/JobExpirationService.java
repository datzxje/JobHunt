package com.jobhunt.service;

public interface JobExpirationService {
  /**
   * Gửi email nhắc nhở cho các job sắp hết hạn trong 1 ngày
   * Method này sẽ được gọi bởi scheduled job hàng ngày
   */
  void sendExpirationReminders();

  /**
   * Đánh dấu các job đã hết hạn thành expired = true
   * Method này sẽ được gọi bởi scheduled job hàng ngày
   */
  void markExpiredJobs();

  /**
   * Xử lý toàn bộ quy trình kiểm tra job hết hạn
   * Bao gồm gửi reminder và đánh dấu expired
   */
  void processJobExpiration();

  /**
   * Gửi email thông báo cho các job đã hết hạn hôm qua
   * Method này sẽ được gọi bởi scheduled job hàng ngày
   */
  void sendExpiredNotifications();

  /**
   * Gửi cả reminder và notification email cùng lúc
   * Method này dùng để test hoặc gọi thủ công
   */
  void sendRemindersAndNotifications();
}