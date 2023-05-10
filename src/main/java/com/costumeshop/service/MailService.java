package com.costumeshop.service;

import com.costumeshop.core.sql.entity.User;
import com.costumeshop.core.sql.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender javaMailSender;
    private final PropertyService propertyService;

    public void sendVerificationEmail(User user) throws MessagingException, UnsupportedEncodingException {
        String toAddress = user.getEmail();
        String fromAddress = propertyService.getProperty("mail.address.from");;
        String senderName = propertyService.getProperty("site.name");
        String subject = propertyService.getProperty("mail.subject");
        String content = propertyService.getProperty("mail.content");

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(fromAddress, senderName);
        helper.setTo(toAddress);
        helper.setSubject(subject);

        String siteUrl = propertyService.getProperty("site.url");
        String verificationUrl = siteUrl + "/verify?token=" + user.getVerificationToken();

        String contentWithUsernameAndUrl = content.replace("[url]", verificationUrl)
                .replace("[username]", user.getUsername());

        helper.setText(contentWithUsernameAndUrl, true);

        javaMailSender.send(message);
    }
}
