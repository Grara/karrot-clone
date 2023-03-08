package com.karrotclone.api;

import com.karrotclone.dto.VerifyEmailVO;
import com.karrotclone.service.email.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
public class EmailController {

    private final EmailService emailService;

    @GetMapping("api/v1/{email}")
    public void sendVerificationMail(@PathVariable() String email) throws Exception{
        emailService.sendRegisterMessage(email);
        System.out.println("send verification mail to " + email);
    }

    @GetMapping("api/v1/verifyemail")
    @ResponseBody
    public String verfiyEmail(@RequestBody VerifyEmailVO verifyEmailVO){
        return emailService.verifyEmail(verifyEmailVO);
    }
}
