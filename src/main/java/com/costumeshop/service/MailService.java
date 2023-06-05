package com.costumeshop.service;

import com.costumeshop.core.sql.entity.User;
import com.costumeshop.model.dto.CartItemDTO;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender javaMailSender;
    private final PropertyService propertyService;

    public void sendVerificationEmail(User user) throws MessagingException, UnsupportedEncodingException {
        String content = propertyService.getProperty("mail.content.registration");
        String subject = propertyService.getProperty("mail.subject.registration");
        EmailData emailData = prepareEmailData(user, content, subject);
        String verificationUrl = emailData.siteUrl() + "/registration-verification?token=" + user.getVerificationToken();

        MimeMessage message = prepareMessage(user, emailData, verificationUrl);

        javaMailSender.send(message);
    }

    public void sendPaymentTransactionSuccessEmail(User user, List<CartItemDTO> cartItems) throws MessagingException, UnsupportedEncodingException {
        String content = propertyService.getProperty("mail.content.transaction");
        String subject = propertyService.getProperty("mail.subject.transaction");
        String itemTable = prepareItemTable(cartItems);
        String contentWithItemTable = content.replace("[items]", itemTable);
        EmailData emailData = prepareEmailData(user, contentWithItemTable, subject);
        MimeMessage message = prepareMessage(user, emailData, "");

        javaMailSender.send(message);
    }

    private String prepareItemTable(List<CartItemDTO> cartItems) {
        String style = " style=\"border: 1px solid black;\"";
        String table = "<table" + style + "><tr><th"
                + style + ">Item title:</th><th"
                + style + ">Amount:</th><th"
                + style + ">Price</th></tr>";

        for (CartItemDTO cartItem : cartItems) {
            table = table.concat(
                    "<tr><td" + style + ">"
                    + cartItem.getTitle()
                    + "</td><td" + style + ">"
                    + cartItem.getItems().size()
                    + "</td><td" + style + ">"
                    + cartItem.getPrice().toString()
                    + "</td></tr>");
        }
        return table.concat("</table>");
    }

    private MimeMessage prepareMessage(User user, EmailData emailData,
                                       String siteUrl) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(emailData.fromAddress(), emailData.senderName());
        helper.setTo(emailData.toAddress());
        helper.setSubject(emailData.subject());

        String contentWithUsernameAndUrl = emailData.content().replace("[url]", siteUrl)
                .replace("[username]", user.getUsername());

        helper.setText(contentWithUsernameAndUrl, true);
        return message;
    }

    private EmailData prepareEmailData(User user, String content, String subject) {
        return EmailData.builder()
                .toAddress(user.getEmail())
                .fromAddress(propertyService.getProperty("mail.address.from"))
                .senderName(propertyService.getProperty("site.name"))
                .subject(subject)
                .content(content)
                .siteUrl(propertyService.getProperty("site.url"))
                .build();
    }

    @Builder
    record EmailData(String toAddress, String fromAddress, String senderName, String subject, String content,
                     String siteUrl) {
    }
}
