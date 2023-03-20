package com.karrotclone.config.jwt;

import com.karrotclone.utils.CookieUtil;
import com.karrotclone.utils.RedisUtil;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {


    private final JwtTokenProvider jwtTokenProvider;
    private final RedisUtil redisUtil;
    private final CookieUtil cookieUtil;
    private final UserDetailsService userDetailsService;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        final Cookie jwtToken = cookieUtil.getCookie(request, jwtTokenProvider.ACCESS_TOKEN_NAME);
        // 인증이 필요한 부분에 매번 이필터가 실행
        String email = null;
        String jwt = null;
        String refreshJwt = null;
        String refreshUserEmail = null;
        // 엑세스 토큰을 1차적으로 가져와서 쿠키에 저장한 뒤에, 만료 되었을시 리프레쉬 토큰 확인후 엑세스 토큰 재발급
        try{
            if(jwtToken != null){
                jwt = jwtToken.getValue();
                email = jwtTokenProvider.getEmail(jwt);
            } else{
                logger.warn("Cannot find access token");
            }
            if(email != null){
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                if(jwtTokenProvider.validateToken(jwt)){
                    logger.info("validateToken : " + jwt);
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                            = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                }
            }else{
                logger.warn("Cannot find username from access token");
            }
        } catch (ExpiredJwtException e){
            String refreshToken = cookieUtil.getCookie(request, jwtTokenProvider.REFRESH_TOKEN_NAME).getValue();
            if(refreshToken != null){
                refreshJwt = refreshToken;
            } else{
                logger.warn("Cannot find refresh token");
            }
        }catch( Exception e){

        }
        try{
            // 리프래쉬 토큰이 만료되지 않았을 때 레디스에서 유저 이름을 찾아서 액세스 토큰을 발급해 로그인
            if(refreshJwt != null){
                refreshUserEmail = redisUtil.getData(refreshJwt);
                if(refreshUserEmail.equals(jwtTokenProvider.getEmail(refreshJwt))){
                    UserDetails userDetails = userDetailsService.loadUserByUsername(refreshUserEmail);
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null,userDetails.getAuthorities() );
                    usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                    Cookie newAccessToken = cookieUtil.creatCookie(jwtTokenProvider.ACCESS_TOKEN_NAME, jwtTokenProvider.doGenerateToken(refreshUserEmail, jwtTokenProvider.ACCESS_TOKEN_EXPIRE_TIME));
                    response.addCookie(newAccessToken);
                }
            } else{
                logger.warn("Cannot find refresh Token");
            }
        } catch (ExpiredJwtException e){

        }

        filterChain.doFilter(request, response);
    }
}
