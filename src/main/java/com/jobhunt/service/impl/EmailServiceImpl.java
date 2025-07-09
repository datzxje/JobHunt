package com.jobhunt.service.impl;

import com.jobhunt.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

  private final JavaMailSender mailSender;

  @Override
  public void sendJobExpirationReminderEmail(String recipientEmail, String companyName, String jobTitle, Long jobId,
      String expirationDate) {
    try {
      MimeMessage message = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

      helper.setTo(recipientEmail);
      helper.setSubject("🚨 QUAN TRỌNG: Tin tuyển dụng hết hạn NGÀY MAI - " + jobTitle);

      String htmlContent = createReminderEmailHtml(companyName, jobTitle, jobId, expirationDate);
      helper.setText(htmlContent, true);

      mailSender.send(message);
      log.info("Job expiration reminder email sent successfully to: {} for job ID: {}", recipientEmail, jobId);
    } catch (Exception e) {
      log.error("Failed to send job expiration reminder email to: {} for job ID: {}", recipientEmail, jobId, e);
      throw new RuntimeException("Failed to send email", e);
    }
  }

  @Override
  public void sendJobExpiredNotificationEmail(String recipientEmail, String companyName, String jobTitle, Long jobId) {
    try {
      MimeMessage message = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

      helper.setTo(recipientEmail);
      helper.setSubject("📢 Thông báo: Tin tuyển dụng đã hết hạn - " + jobTitle);

      String htmlContent = createExpiredNotificationEmailHtml(companyName, jobTitle, jobId);
      helper.setText(htmlContent, true);

      mailSender.send(message);
      log.info("Job expired notification email sent successfully to: {} for job ID: {}", recipientEmail, jobId);
    } catch (Exception e) {
      log.error("Failed to send job expired notification email to: {} for job ID: {}", recipientEmail, jobId, e);
      throw new RuntimeException("Failed to send email", e);
    }
  }

  private String createReminderEmailHtml(String companyName, String jobTitle, Long jobId, String expirationDate) {
    return String.format("""
        <!DOCTYPE html>
        <html lang="vi">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Thông báo hết hạn tin tuyển dụng</title>
            <style>
                * {
                    margin: 0;
                    padding: 0;
                    box-sizing: border-box;
                }
                body {
                    font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
                    line-height: 1.6;
                    color: #1f2937;
                    background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%);
                    min-height: 100vh;
                    padding: 20px;
                }
                .email-wrapper {
                    max-width: 600px;
                    margin: 0 auto;
                    background: rgba(255, 255, 255, 0.95);
                    backdrop-filter: blur(10px);
                    border-radius: 20px;
                    overflow: hidden;
                    box-shadow: 0 25px 50px rgba(0, 0, 0, 0.15);
                }
                .header {
                    background: linear-gradient(135deg, #ff6b6b 0%%, #ee5a24 100%%);
                    color: white;
                    padding: 40px 30px;
                    text-align: center;
                    position: relative;
                    overflow: hidden;
                }
                .header::before {
                    content: '';
                    position: absolute;
                    top: -50%%;
                    left: -50%%;
                    width: 200%%;
                    height: 200%%;
                    background: radial-gradient(circle, rgba(255,255,255,0.1) 0%%, transparent 70%%);
                    animation: pulse 4s ease-in-out infinite;
                }
                @keyframes pulse {
                    0%%, 100%% { transform: scale(1); opacity: 0.5; }
                    50%% { transform: scale(1.1); opacity: 0.8; }
                }
                .logo {
                    font-size: 26px;
                    font-weight: 800;
                    margin-bottom: 5px;
                    position: relative;
                    z-index: 1;
                }
                .alert-icon {
                    font-size: 40px;
                    margin: 10px 0;
                    animation: bounce 2s ease-in-out infinite;
                    position: relative;
                    z-index: 1;
                }
                @keyframes bounce {
                    0%%, 20%%, 50%%, 80%%, 100%% { transform: translateY(0); }
                    40%% { transform: translateY(-10px); }
                    60%% { transform: translateY(-5px); }
                }
                .alert-title {
                    font-size: 20px;
                    font-weight: 700;
                    letter-spacing: 0.5px;
                    position: relative;
                    z-index: 1;
                }
                .content {
                    padding: 25px 20px;
                }
                .greeting {
                    font-size: 16px;
                    margin-bottom: 15px;
                    color: #374151;
                }
                .main-message {
                    font-size: 15px;
                    margin-bottom: 20px;
                    color: #6b7280;
                }
                .job-card {
                    background: linear-gradient(135deg, #f8fafc 0%%, #e2e8f0 100%%);
                    border-radius: 16px;
                    padding: 30px;
                    margin: 30px 0;
                    border: 1px solid #e5e7eb;
                    position: relative;
                    overflow: hidden;
                }
                .job-card::before {
                    content: '';
                    position: absolute;
                    top: 0;
                    left: 0;
                    right: 0;
                    height: 4px;
                    background: linear-gradient(90deg, #3b82f6, #8b5cf6, #06b6d4);
                }
                .job-title {
                    font-size: 18px;
                    font-weight: 700;
                    color: #1f2937;
                    margin-bottom: 15px;
                    display: flex;
                    align-items: center;
                    gap: 8px;
                }
                .job-detail {
                    display: flex;
                    align-items: center;
                    gap: 10px;
                    margin: 8px 0;
                    padding: 4px 0;
                    color: #4b5563;
                    font-size: 14px;
                }
                .job-detail-icon {
                    font-size: 18px;
                    width: 24px;
                    text-align: center;
                }
                .warning-card {
                    background: linear-gradient(135deg, #fef3c7 0%%, #fde68a 100%%);
                    border-radius: 16px;
                    padding: 25px;
                    margin: 30px 0;
                    border-left: 6px solid #f59e0b;
                    position: relative;
                }
                .warning-title {
                    color: #92400e;
                    font-weight: 700;
                    font-size: 18px;
                    margin-bottom: 15px;
                    display: flex;
                    align-items: center;
                    gap: 10px;
                }
                .warning-list {
                    list-style: none;
                    margin: 0;
                    padding: 0;
                }
                .warning-list li {
                    color: #78350f;
                    margin: 6px 0;
                    padding-left: 20px;
                    position: relative;
                    font-size: 14px;
                }
                .warning-list li::before {
                    content: '▸';
                    position: absolute;
                    left: 0;
                    color: #f59e0b;
                    font-weight: bold;
                    font-size: 16px;
                }
                .cta-section {
                    text-align: center;
                    margin: 40px 0;
                }
                .cta-button {
                    display: inline-block;
                    background: linear-gradient(135deg, #3b82f6 0%%, #1d4ed8 100%%);
                    color: white;
                    padding: 16px 32px;
                    text-decoration: none;
                    border-radius: 12px;
                    font-weight: 600;
                    font-size: 16px;
                    letter-spacing: 0.5px;
                    transition: all 0.3s ease;
                    box-shadow: 0 10px 25px rgba(59, 130, 246, 0.3);
                }
                .cta-button:hover {
                    transform: translateY(-2px);
                    box-shadow: 0 15px 35px rgba(59, 130, 246, 0.4);
                }
                .footer {
                    background: #f9fafb;
                    padding: 20px;
                    text-align: center;
                    border-top: 1px solid #e5e7eb;
                    margin-top: 20px;
                }
                .footer-brand {
                    font-weight: 700;
                    color: #374151;
                    margin-bottom: 10px;
                }
                .footer-note {
                    font-size: 13px;
                    color: #9ca3af;
                    line-height: 1.5;
                }
                .highlight {
                    color: #dc2626;
                    font-weight: 700;
                }
                @media (max-width: 640px) {
                    .email-wrapper {
                        margin: 10px;
                        border-radius: 16px;
                    }
                    .header {
                        padding: 30px 20px;
                    }
                    .content {
                        padding: 30px 20px;
                    }
                    .job-card {
                        padding: 20px;
                    }
                    .logo {
                        font-size: 28px;
                    }
                    .alert-icon {
                        font-size: 48px;
                    }
                }
            </style>
        </head>
        <body>
            <div class="email-wrapper">
                <div class="header">
                    <div class="logo">🏢 JobHunt</div>
                    <div class="alert-icon">🚨</div>
                    <div class="alert-title">THÔNG BÁO QUAN TRỌNG</div>
                </div>

                <div class="content">
                    <div class="greeting">
                        Xin chào <strong>%s</strong>,
                    </div>

                    <div class="main-message">
                        Chúng tôi muốn thông báo rằng tin tuyển dụng sau đây sẽ <span class="highlight">HẾT HẠN VÀO NGÀY MAI</span>:
                    </div>

                    <div class="job-card">
                        <div class="job-title">
                            <span>📋</span>
                            <span>%s</span>
                        </div>
                        <div class="job-detail">
                            <span class="job-detail-icon">🔢</span>
                            <span><strong>ID công việc:</strong> %d</span>
                        </div>
                        <div class="job-detail">
                            <span class="job-detail-icon">⏰</span>
                            <span><strong>Ngày hết hạn:</strong> %s</span>
                        </div>
                    </div>

                    <div class="warning-card">
                        <div class="warning-title">
                            <span>⚠️</span>
                            <span>Lưu ý quan trọng:</span>
                        </div>
                        <ul class="warning-list">
                            <li>Đây là ngày cuối cùng để nhận ứng tuyển!</li>
                            <li>Vui lòng kiểm tra và gia hạn ngay nếu muốn tiếp tục nhận ứng viên</li>
                            <li>Sau ngày mai, tin tuyển dụng sẽ tự động bị ẩn khỏi danh sách tìm kiếm</li>
                        </ul>
                    </div>

                    <div class="cta-section">
                        <a href="#" class="cta-button">🔄 Gia hạn tin tuyển dụng</a>
                    </div>
                </div>

                <div class="footer">
                    <div class="footer-brand">Trân trọng,<br>JobHunt Team</div>
                    <div class="footer-note">
                        Email này được gửi tự động. Vui lòng không trả lời email này.
                    </div>
                </div>
            </div>
        </body>
        </html>
        """, companyName, jobTitle, jobId, expirationDate);
  }

  private String createExpiredNotificationEmailHtml(String companyName, String jobTitle, Long jobId) {
    return String.format("""
        <!DOCTYPE html>
        <html lang="vi">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Thông báo tin tuyển dụng đã hết hạn</title>
            <style>
                * {
                    margin: 0;
                    padding: 0;
                    box-sizing: border-box;
                }
                body {
                    font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
                    line-height: 1.6;
                    color: #1f2937;
                    background: linear-gradient(135deg, #a8edea 0%%, #fed6e3 100%%);
                    min-height: 100vh;
                    padding: 20px;
                }
                .email-wrapper {
                    max-width: 600px;
                    margin: 0 auto;
                    background: rgba(255, 255, 255, 0.95);
                    backdrop-filter: blur(10px);
                    border-radius: 20px;
                    overflow: hidden;
                    box-shadow: 0 25px 50px rgba(0, 0, 0, 0.15);
                }
                .header {
                    background: linear-gradient(135deg, #64748b 0%%, #475569 100%%);
                    color: white;
                    padding: 40px 30px;
                    text-align: center;
                    position: relative;
                    overflow: hidden;
                }
                .header::before {
                    content: '';
                    position: absolute;
                    top: -50%%;
                    left: -50%%;
                    width: 200%%;
                    height: 200%%;
                    background: radial-gradient(circle, rgba(255,255,255,0.1) 0%%, transparent 70%%);
                    animation: gentle-pulse 6s ease-in-out infinite;
                }
                @keyframes gentle-pulse {
                    0%%, 100%% { opacity: 0.3; }
                    50%% { opacity: 0.6; }
                }
                .logo {
                    font-size: 26px;
                    font-weight: 800;
                    margin-bottom: 5px;
                    position: relative;
                    z-index: 1;
                }
                .status-icon {
                    font-size: 40px;
                    margin: 10px 0;
                    position: relative;
                    z-index: 1;
                    animation: fade-in 1s ease-in;
                }
                @keyframes fade-in {
                    from { opacity: 0; transform: scale(0.8); }
                    to { opacity: 1; transform: scale(1); }
                }
                .status-title {
                    font-size: 20px;
                    font-weight: 700;
                    letter-spacing: 0.5px;
                    position: relative;
                    z-index: 1;
                }
                .content {
                    padding: 25px 20px;
                }
                .greeting {
                    font-size: 16px;
                    margin-bottom: 15px;
                    color: #374151;
                }
                .main-message {
                    font-size: 15px;
                    margin-bottom: 20px;
                    color: #6b7280;
                }
                .job-card {
                    background: linear-gradient(135deg, #f1f5f9 0%%, #e2e8f0 100%%);
                    border-radius: 16px;
                    padding: 30px;
                    margin: 30px 0;
                    border: 1px solid #cbd5e1;
                    position: relative;
                    overflow: hidden;
                }
                .job-card::before {
                    content: '';
                    position: absolute;
                    top: 0;
                    left: 0;
                    right: 0;
                    height: 4px;
                    background: linear-gradient(90deg, #64748b, #94a3b8, #cbd5e1);
                }
                .job-title {
                    font-size: 18px;
                    font-weight: 700;
                    color: #1f2937;
                    margin-bottom: 15px;
                    display: flex;
                    align-items: center;
                    gap: 8px;
                }
                .job-detail {
                    display: flex;
                    align-items: center;
                    gap: 10px;
                    margin: 8px 0;
                    padding: 4px 0;
                    color: #4b5563;
                    font-size: 14px;
                }
                .job-detail-icon {
                    font-size: 18px;
                    width: 24px;
                    text-align: center;
                }
                .info-card {
                    background: linear-gradient(135deg, #dbeafe 0%%, #bfdbfe 100%%);
                    border-radius: 16px;
                    padding: 25px;
                    margin: 30px 0;
                    border-left: 6px solid #3b82f6;
                    position: relative;
                }
                .info-title {
                    color: #1e40af;
                    font-weight: 700;
                    font-size: 18px;
                    margin-bottom: 15px;
                    display: flex;
                    align-items: center;
                    gap: 10px;
                }
                .info-list {
                    list-style: none;
                    margin: 0;
                    padding: 0;
                }
                .info-list li {
                    color: #1e3a8a;
                    margin: 6px 0;
                    padding-left: 20px;
                    position: relative;
                    font-size: 14px;
                }
                .info-list li::before {
                    content: '▸';
                    position: absolute;
                    left: 0;
                    color: #3b82f6;
                    font-weight: bold;
                    font-size: 16px;
                }
                .cta-section {
                    text-align: center;
                    margin: 40px 0;
                }
                .cta-button {
                    display: inline-block;
                    background: linear-gradient(135deg, #10b981 0%%, #059669 100%%);
                    color: white;
                    padding: 16px 32px;
                    text-decoration: none;
                    border-radius: 12px;
                    font-weight: 600;
                    font-size: 16px;
                    letter-spacing: 0.5px;
                    transition: all 0.3s ease;
                    box-shadow: 0 10px 25px rgba(16, 185, 129, 0.3);
                }
                .cta-button:hover {
                    transform: translateY(-2px);
                    box-shadow: 0 15px 35px rgba(16, 185, 129, 0.4);
                }
                .footer {
                    background: #f9fafb;
                    padding: 20px;
                    text-align: center;
                    border-top: 1px solid #e5e7eb;
                    margin-top: 20px;
                }
                .footer-brand {
                    font-weight: 700;
                    color: #374151;
                    margin-bottom: 10px;
                }
                .footer-note {
                    font-size: 13px;
                    color: #9ca3af;
                    line-height: 1.5;
                }
                .highlight {
                    color: #dc2626;
                    font-weight: 700;
                }
                @media (max-width: 640px) {
                    .email-wrapper {
                        margin: 10px;
                        border-radius: 16px;
                    }
                    .header {
                        padding: 30px 20px;
                    }
                    .content {
                        padding: 30px 20px;
                    }
                    .job-card {
                        padding: 20px;
                    }
                    .logo {
                        font-size: 28px;
                    }
                    .status-icon {
                        font-size: 48px;
                    }
                }
            </style>
        </head>
        <body>
            <div class="email-wrapper">
                <div class="header">
                    <div class="logo">🏢 JobHunt</div>
                    <div class="status-icon">📢</div>
                    <div class="status-title">THÔNG BÁO HẾT HẠN</div>
                </div>

                <div class="content">
                    <div class="greeting">
                        Xin chào <strong>%s</strong>,
                    </div>

                    <div class="main-message">
                        Chúng tôi muốn thông báo rằng tin tuyển dụng sau đây đã <span class="highlight">HẾT HẠN</span>:
                    </div>

                    <div class="job-card">
                        <div class="job-title">
                            <span>📋</span>
                            <span>%s</span>
                        </div>
                        <div class="job-detail">
                            <span class="job-detail-icon">🔢</span>
                            <span><strong>ID công việc:</strong> %d</span>
                        </div>
                    </div>

                    <div class="info-card">
                        <div class="info-title">
                            <span>ℹ️</span>
                            <span>Thông tin:</span>
                        </div>
                        <ul class="info-list">
                            <li>Tin tuyển dụng này đã được đánh dấu là hết hạn</li>
                            <li>Ứng viên sẽ không thể thấy tin tuyển dụng này nữa</li>
                            <li>Bạn có thể tạo tin tuyển dụng mới hoặc liên hệ với chúng tôi</li>
                        </ul>
                    </div>

                    <div class="cta-section">
                        <a href="#" class="cta-button">➕ Tạo tin tuyển dụng mới</a>
                    </div>
                </div>

                <div class="footer">
                    <div class="footer-brand">Trân trọng,<br>JobHunt Team</div>
                    <div class="footer-note">
                        Email này được gửi tự động. Vui lòng không trả lời email này.
                    </div>
                </div>
            </div>
        </body>
        </html>
        """, companyName, jobTitle, jobId);
  }
}