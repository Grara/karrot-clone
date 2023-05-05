package com.karrotclone.controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    @GetMapping("/") //이미지 업로드 테스트용 화면
    public String test(){
        return "test";
    }

    @GetMapping("/chat-test")
    public String chatTest(){
        return "chat-test";
    }

}
