package org.healthapps.birthdefects.web;

import org.healthapps.birthdefects.model.ErrorMessages;
import org.healthapps.birthdefects.model.TextValidator;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class Emailer {
    public static String FROM_EMAIL = "foo@bar.com";
    private static final String REGEX_VALID_BODY = "[\\w]+";
    private static final int MAX_BODY_LENGTH = 1000;
    private static final int MAX_SUBJECT_LENGTH = 30;
    private static final int MIN_LENGTH = 1;

    public boolean send(String toEmail, String subject, String message) {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);
        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(FROM_EMAIL));
            msg.addRecipient(Message.RecipientType.TO,
                    new InternetAddress(toEmail));
            msg.setSubject(subject);
            msg.setText(message);
            Transport.send(msg);
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    public ErrorMessages validate(String emailBody, String subject) {
        final ErrorMessages errors =  TextValidator.validate(subject, MAX_SUBJECT_LENGTH, MIN_LENGTH, "subject");
        errors.addAll(TextValidator.validate(emailBody, MAX_BODY_LENGTH, MIN_LENGTH, "emailBody", REGEX_VALID_BODY, "",
                "invalidEmailBody"));
        return errors;
    }
}
