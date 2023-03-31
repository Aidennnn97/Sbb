package com.mysite.sbb.mail;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender javaMailSender;
    private String ePassword;
    public MimeMessage createMessage(String to) throws UnsupportedEncodingException, MessagingException {

        MimeMessage message = javaMailSender.createMimeMessage();

        message.addRecipients(Message.RecipientType.TO, to);
        message.setSubject("Sbb 임시 비밀번호");

        String msg = "";
        msg += "<div style='margin:100px;'>";
        msg += "<div align='center' style='border:1px solid black; font-family:verdana';>";
        msg += "<h3 style='color:blue;'>임시 비밀번호입니다.</h3>";
        msg += "<div style='font-size:130%'>";
        msg += "CODE : <strong>";
        msg += ePassword + "</strong><div><br/> ";
        msg += "</div>";

        message.setText(msg, "utf-8", "html");
        message.setFrom(new InternetAddress("jea5158@gmail.com", "sbb_Admin"));

        return message;
    }

    public void sendSimpleMessage(String to, String pw){
        this.ePassword = pw;
        MimeMessage message;
        try {
            message = createMessage(to);
        } catch (UnsupportedEncodingException | MessagingException e){
            e.printStackTrace();
            throw new EmailException("이메일 생성 에러");
        }
        try{
            javaMailSender.send(message);
        } catch (MailException e){
            e.printStackTrace();
            throw new EmailException("이메일 전송 에러");
        }
    }
}

