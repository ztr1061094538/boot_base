package com.tg.sbootshrio.util;

import com.tg.sbootshrio.pojo.MailBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.internet.MimeMessage;
import java.io.File;

/**
 * Created by admin on 2019/7/26.
 */
@Component
public class EmailUtil {
    @Value("${spring.mail.username}")
    private String MAIL_SENDER; //邮件发送者

    @Autowired
    private JavaMailSender javaMailSender;
    /**
     * 发送文本邮件
     *
     * @param mailBean
     */
    public  Boolean sendSimpleMail(MailBean mailBean) {

        try {
            SimpleMailMessage mailMessage= new SimpleMailMessage();
            mailMessage.setFrom(MAIL_SENDER);
            mailMessage.setTo(mailBean.getRecipient());
            mailMessage.setSubject(mailBean.getSubject());
            mailMessage.setText(mailBean.getContent());
            //mailMessage.copyTo(copyTo);

            javaMailSender.send(mailMessage);
          //  MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            return true;
        } catch (Exception e) {
            System.out.println("发送失败===");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 发送html格式的邮件
     */

    public  void sendHtmlMail(MailBean mailBean) {
        MimeMessage mimeMessage=null;
        try {
            mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setFrom(MAIL_SENDER);
            mimeMessageHelper.setTo(mailBean.getRecipient());
            mimeMessageHelper.setSubject(mailBean.getSubject());
            mimeMessageHelper.setText(mailBean.getContent());
            //mailMessage.copyTo(copyTo);
            StringBuilder sb = new StringBuilder();
            sb.append("<h1>SpirngBoot测试邮件HTML</h1>")
                    .append("\"<p style='color:#F00'>你是真的太棒了！</p>")
                    .append("<p style='text-align:right'>右对齐</p>");
            mimeMessageHelper.setText(sb.toString(), true);
            javaMailSender.send(mimeMessage);


        } catch (Exception e) {
            System.out.println("发送失败"+ e.getMessage());
            System.out.println("发送失败==="+ e);

        }
    }


    /**
     * 发送带附件的邮件
     */
    public  void sendAppendixMail(MailBean mailBean) {
        MimeMessage mimeMessage=null;
        try {
            mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setFrom(MAIL_SENDER);
            mimeMessageHelper.setTo(mailBean.getRecipient());
            mimeMessageHelper.setSubject(mailBean.getSubject());
            mimeMessageHelper.setText(mailBean.getContent());
            FileSystemResource file = new FileSystemResource(new File("C:/photo/timg.jpg"));
//src/main/resources/static/timg.jpg
            System.out.println("file="+file);
            mimeMessageHelper.addAttachment("timg.jpg", file);
            javaMailSender.send(mimeMessage);



        } catch (Exception e) {
            System.out.println("发送失败"+ e.getMessage());
            System.out.println("发送失败==="+ e);

        }
    }

}
