package kanbancalendar.project.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
public class EmailService{

    private final JavaMailSender javaMailSender;

    @Autowired
    public EmailService(JavaMailSender javaMailSender){
        this.javaMailSender = javaMailSender;
    }

    //Wyślij email
    public void sendEmail(String to, String subject, String text) throws MessagingException {
        MimeMessage msg = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(msg,true);
        helper.setFrom("noreply@kanbancalendar.com");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text,true);
        javaMailSender.send(msg);
    }

}
