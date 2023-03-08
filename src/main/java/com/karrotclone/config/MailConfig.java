package com.karrotclone.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfig {
    private String id= "93knds@gmail.com";

    private String password = "ziztxiexqrrsitfq";

    private String host= "smtp.gmail.com";

    private int port = 587;

    @Bean
    public JavaMailSender javaMailService(){
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();

        javaMailSender.setHost(host);
        javaMailSender.setUsername(id);
        javaMailSender.setPassword(password);
        javaMailSender.setPort(port);
        javaMailSender.setJavaMailProperties(getMailProperties());
        javaMailSender.setDefaultEncoding("UTF-8");
        return javaMailSender;
    }

    private Properties getMailProperties(){
        Properties properties = new Properties();
        properties.setProperty("mail.smtp.auth", "true"); // smtp 인증
        properties.setProperty("mail.smtp.starttls.enable", "true"); // smtp starttls 사용
        //properties.setProperty("mail.debug", "true"); // 디버그 사용
        //properties.setProperty("mail.smtp.ssl.trust","smtp.mailplug.co.kr"); // ssl 인증 서버 주소
        //properties.setProperty("mail.smtp.ssl.enable","true"); // ssl 사용
        return properties;
    }
}
