package com.chris.sd_assignment1.model.services;

import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
import java.util.Properties;

public class EmailService {

    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SENDER_EMAIL = "cristinaciors@gmail.com";
    private static final String SENDER_PASSWORD = "hzhj ldlk bwbw rzaa";

    public void sendEmailWithAttachment(String toEmail, String subject, String bodyText, String filePath) {
        ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(getClass().getClassLoader());

        try {
            System.out.println("DEBUG: Starting email dispatch process...");
            System.out.println("DEBUG: Target recipient: " + toEmail);

            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", SMTP_HOST);
            props.put("mail.smtp.port", "587");

            props.put("mail.smtp.connectiontimeout", "10000");
            props.put("mail.smtp.timeout", "10000");

            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(SENDER_EMAIL, SENDER_PASSWORD);
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SENDER_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject);

            MimeBodyPart textBodyPart = new MimeBodyPart();
            textBodyPart.setText(bodyText);

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(textBodyPart);

            if (filePath != null && !filePath.isEmpty()) {
                File file = new File(filePath);
                if (file.exists()) {
                    MimeBodyPart attachmentPart = new MimeBodyPart();
                    attachmentPart.attachFile(file);
                    multipart.addBodyPart(attachmentPart);
                    System.out.println("DEBUG: Attachment found and added: " + filePath);
                } else {
                    System.out.println("DEBUG: No attachment found at path: " + filePath + " (Sending text only)");
                }
            }

            message.setContent(multipart);

            System.out.println("DEBUG: Attempting to connect to SMTP server...");
            Transport.send(message);
            System.out.println("DEBUG: Email sent SUCCESSFULLY to " + toEmail);

        } catch (Exception e) {
            System.err.println("DEBUG: !!! EMAIL ERROR !!!");
            System.err.println("Error Message: " + e.getMessage());
            e.printStackTrace();
        } finally {
            Thread.currentThread().setContextClassLoader(originalClassLoader);
        }
    }
}