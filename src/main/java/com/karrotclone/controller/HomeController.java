package com.karrotclone.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/") //이미지 업로드 테스트용 화면
    public String test(){
        return "test";
    }
}
