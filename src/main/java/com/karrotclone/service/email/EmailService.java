package com.karrotclone.service.email;

import com.karrotclone.dto.VerifyEmailVO;
import com.karrotclone.utils.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;

    private final RedisUtil redisUtil;

    private final SpringTemplateEngine templateEngine;

    private String id= "93knds@gmail.com";
    private String ePw;

    private String tPw;

    public MimeMessage registerMessage(String to) throws MessagingException, UnsupportedEncodingException {
        createKey();
        log.info("보내는 대상 : " + to);
        log.info("인증 번호 : " + ePw);

        MimeMessage message = this.javaMailSender.createMimeMessage();
        message.addRecipients(MimeMessage.RecipientType.TO, to);
        message.setSubject("회원 가입 인증 코드 발송 메일입니다.");

        message.setText(setContext("register_mail",ePw), "utf-8","html");
        message.setFrom(new InternetAddress(id, "Tmax_Metaverse"));

        return message;
    }

    public void createKey(){
        StringBuffer key = new StringBuffer();
        Random rnd = new Random();

        for (int i = 0; i< 6; i++){
            key.append((rnd.nextInt(10)));
        }
        ePw = key.toString();
    }

    public String setContext(String template, String code){
        Context context = new Context();
        context.setVariable("code", code);
        return templateEngine.process(template, context);
    }

    //메일 전송 메소드
    public String sendRegisterMessage(String to) throws Exception{
        MimeMessage message = registerMessage(to);
        try{
            redisUtil.setDataExpire(ePw, to, 60 * 3L);
            javaMailSender.send(message);
        } catch (MailException e){
            e.printStackTrace();
            throw new IllegalArgumentException();
        }

        return ePw;
    }

    public String verifyEmail(VerifyEmailVO verifyEmailVO){
        String Email = redisUtil.getData(verifyEmailVO.getKey());
        if(Email != verifyEmailVO.getEmail()){
            throw new AccessDeniedException("Wrong Credential");
        }
        redisUtil.deleteData(verifyEmailVO.getKey());
        return ePw;
    }
}
