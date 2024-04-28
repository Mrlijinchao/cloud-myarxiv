package com.lijinchao.utils.mail;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Component
@Slf4j
public class SendMail {

    private static Auth auth = new Auth();

    public static void sendMail(String email,String content,String mailSubject) throws MessagingException {

        Properties pro = new Properties();

        pro.setProperty("mail.host", "smtp.qq.com");//mail.host=smtp.qq.com

        pro.setProperty("mail.transport.protocol", "smtp");

        pro.setProperty("mail.smtp.auth", "true");

        pro.setProperty("mail.smtp.port","465");

        pro.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        System.out.println("port:"+pro.getProperty("mail.smtp.port"));

        Session session= Session.getDefaultInstance(pro,auth);

        Transport ts=session.getTransport();

        ts.connect("smtp.qq.com","2751488527@qq.com","grexmxsovaqndhbe");

        MimeMessage message=new MimeMessage(session);

        message.setFrom(new InternetAddress("2751488527@qq.com"));

//        message.setRecipient(Message.RecipientType.TO,new InternetAddress("li2116639781@sina.com"));
//        message.setRecipient(Message.RecipientType.TO,new InternetAddress("2751488527@qq.com"));
        message.setRecipient(Message.RecipientType.TO,new InternetAddress(email));

        message.setSubject(mailSubject);

        message.setContent(content,"text/html;charset=utf-8");

        ts.sendMessage(message, message.getAllRecipients());

        ts.close();
        log.info("邮件发送成功");
    }

    public static String getEndorsementContent(String username,String userSubject,String endorsementCode){
            String content ="<!DOCTYPE html>\r\n"
                    + "<HTML>\r\n"
                    + "<HEAD>\r\n"
                    + "<META  charset=\" gbk\">\r\n"
                    + "<TITLE>登录验证码邮件</TITLE>\r\n"
                    + "</HEAD>\r\n"
                    + "<BODY>\r\n"
                    + "    <p>("+username+" should forward this email to someone who's</p>\r\n"
                    + "    <p>registered as an endorser for the "+userSubject+" subject class of myArXiv.)</p>\r\n"
                    + "    <p></p>\r\n"
                    + "    <p>"+username+" requests your endorsement to submit an article to the</p>\r\n"
                    + "    <p>"+userSubject+" section of myArXiv. To tell us that you would (or would not) like to</p>\r\n"
                    + "    <p></p>\r\n"
                    + "    <p>endorse this person, please visit the following URL:</p>\r\n"
                    + "    <p>https://myArxiv.org/auth/endorse</p>\r\n"
                    + "    <p></p>\r\n"
                    + "    <p>and enter the following six-digit alphanumeric string:</p>\r\n"
                    + "    <p>Endorsement Code: "+endorsementCode+"</p>\r\n"
                    + "</BODY>\r\n"
                    + "</HTML>";
            return content;
    }

}
