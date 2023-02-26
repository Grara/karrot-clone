package com.karrotclone.utils;

import com.karrotclone.config.jwt.JwtTokenProvider;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@Service
public class CookieUitls {

    /*
    이 Cookie는 기존에 http에서 사용하던 코드
    ios에서는 다르게 해야될까? 고민좀 해보자.
     */
    public Cookie createCookie(String cookieName, String value){
        Cookie token = new Cookie(cookieName, value);
        token.setHttpOnly(true);
        // 추가로 구성 진행
        token.setMaxAge((int) JwtTokenProvider.ACCESS_TOKEN_EXPIRE_TIME);
        return token;
    }

    public Cookie getCookie(HttpServletRequest request, String cookieName){
        final Cookie[] cookies = request.getCookies();
        if(cookies==null) return null;
        for(Cookie cookie : cookies){
            if(cookie.getName().equals(cookieName))
                return cookie;
        }
        return null;
    }
}
