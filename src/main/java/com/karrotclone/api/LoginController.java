package com.karrotclone.api;

import com.karrotclone.config.jwt.JwtTokenProvider;
import com.karrotclone.domain.Member;
import com.karrotclone.dto.LoginDto;
import com.karrotclone.service.auth.LoginService;
import com.karrotclone.utils.CookieUtil;
import com.karrotclone.utils.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@RestController
public class LoginController {

    private final LoginService loginService;

    private final CookieUtil cookieUtil;

    private final JwtTokenProvider jwtTokenProvider;

    private final RedisUtil redisUtil;

    @PostMapping("api/v1/login")
    public void login(@RequestBody LoginDto user, HttpServletRequest req, HttpServletResponse res){
        try{
            final Member member = loginService.login(user.getEmail(), user.getPassword());
            Cookie accessToken = cookieUtil.creatCookie(jwtTokenProvider.ACCESS_TOKEN_NAME,jwtTokenProvider.generateAccessToken(member));
            accessToken.setMaxAge((int) TimeUnit.MILLISECONDS.toSeconds(jwtTokenProvider.ACCESS_TOKEN_EXPIRE_TIME));
            Cookie refreshToken = cookieUtil.creatCookie(jwtTokenProvider.REFRESH_TOKEN_NAME, jwtTokenProvider.generateRefreshToken(member));
            refreshToken.setMaxAge((int) TimeUnit.MILLISECONDS.toSeconds(jwtTokenProvider.REFRESH_TOKEN_EXPIRE_TIME));
            redisUtil.setDataExpire(refreshToken.getValue(), member.getUsername(), jwtTokenProvider.REFRESH_TOKEN_EXPIRE_TIME);
            res.addCookie(accessToken);
            res.addCookie(refreshToken);

        } catch(Exception e){
            log.error("login error", e);
        }
    }

    @GetMapping("api/v1/logout")
    public String logout(HttpServletRequest req, HttpServletResponse res){
        SecurityContextHolder.clearContext();
        Cookie accessToken = cookieUtil.getCookie(req, jwtTokenProvider.ACCESS_TOKEN_NAME);
        Cookie refreshToken = cookieUtil.getCookie(req, jwtTokenProvider.REFRESH_TOKEN_NAME);
        if(accessToken != null){
            Long expiration = jwtTokenProvider.getExpireTime(accessToken.getValue());
            redisUtil.setBlackList(accessToken.getValue(), "accessToken", expiration-System.currentTimeMillis());
            accessToken.setMaxAge(0);
            res.addCookie(accessToken);
        }
        if(refreshToken != null){
            refreshToken.setMaxAge(0);
            res.addCookie(refreshToken);
            redisUtil.deleteData(refreshToken.getValue());
        }

        return null;
    }
}
