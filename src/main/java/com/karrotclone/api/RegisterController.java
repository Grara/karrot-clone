package com.karrotclone.api;

import com.karrotclone.dto.RegisterDto;
import com.karrotclone.service.auth.RegisterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RequiredArgsConstructor
@Slf4j
@RestController
public class RegisterController {

    private final RegisterService registerService;

    @PostMapping("api/v1/register")
    public void register(final @Valid @RequestBody RegisterDto dto, BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            System.out.println(" Incoming error ");
            return;
        }
        try {
            registerService.register(dto);
            System.out.println("회원 가입이 완료되었습니다.");
        }catch (DataIntegrityViolationException e){
            e.printStackTrace();
            bindingResult.reject("register failed","이미 등록된 메일입니다.");
        } catch (Exception e){
            System.out.println("error = " + e);
            e.printStackTrace();
            bindingResult.reject("register failed", e.getMessage());
        }
    }

    //추후에 다른 로그인 Auth 구현 가능.
}
