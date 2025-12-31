package com.pawpplanet.backend.common.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class MailService {

        private final JavaMailSender mailSender;

        // Gửi mail text đơn giản
        public void sendTextMail(String to, String subject, String content) {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content);
            mailSender.send(message);
        }

        // Gửi mail HTML (chuyên nghiệp hơn)
        public void sendHtmlMail(String to, String subject, String htmlContent) throws MessagingException {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // true = HTML
            mailSender.send(message);
        }

    public void sendVerifyEmail(String to, String token) throws MessagingException {
        String verifyLink = "http://localhost:8080/api/v1/auth/verify-email?token=" + token;

        String templateVerifyEmail ="<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>Xác thực email</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "<h2>Chào bạn!</h2>\n" +
                "<p>Cảm ơn bạn đã đăng ký tài khoản.</p>\n" +
                "<p>Vui lòng nhấn vào nút bên dưới để xác thực email:</p>\n" +
                "<a href=\"{{verify_link}}\"\n" +
                "   style=\"padding: 10px 20px; background-color: #4CAF50; color: white; text-decoration: none;\">\n" +
                "    Xác thực email\n" +
                "</a>\n" +
                "<p>Link sẽ hết hạn sau 1 giờ.</p>\n" +
                "<br>\n" +
                "<p>Trân trọng,</p>\n" +
                "<p>Đội ngũ hệ thống</p>\n" +
                "</body>\n" +
                "</html>\n" +
                "";
        String html = templateVerifyEmail.replace("{{verify_link}}", verifyLink);

        sendHtmlMail(to, "Xác thực email", html);
    }

    public void sendResetPasswordEmail(String to, String token) throws MessagingException {
        String resetLink = "http://localhost:8080/api/v1/auth/reset-password?token=" + token;

        String templateResetPasswordEmail ="<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>Đặt lại mật khẩu</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "<h2>Chào bạn!</h2>\n" +
                "<p>Chúng tôi đã nhận được yêu cầu đặt lại mật khẩu cho tài khoản của bạn.</p>\n" +
                "<p>Vui lòng nhấn vào nút bên dưới để đặt lại mật khẩu:</p>\n" +
                "<a href=\"{{reset_link}}\"\n" +
                "   style=\"padding: 10px 20px; background-color: #f44336; color: white; text-decoration: none;\">\n" +
                "    Đặt lại mật khẩu\n" +
                "</a>\n" +
                "<p>Link sẽ hết hạn sau 1 giờ.</p>\n" +
                "<br>\n" +
                "<p>Trân trọng,</p>\n" +
                "<p>Đội ngũ hệ thống</p>\n" +
                "</body>\n" +
                "</html>\n" +
                "";
        String html = templateResetPasswordEmail.replace("{{reset_link}}", resetLink);

        sendHtmlMail(to, "Đặt lại mật khẩu", html);
    }


}
