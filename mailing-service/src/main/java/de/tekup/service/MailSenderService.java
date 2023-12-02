package de.tekup.service;

import javax.mail.MessagingException;

public interface MailSenderService {
    void sendEmail(String to, String subject, String text) throws MessagingException;
}
