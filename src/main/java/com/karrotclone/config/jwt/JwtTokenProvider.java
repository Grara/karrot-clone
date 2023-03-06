package com.karrotclone.config.jwt;

import com.karrotclone.domain.Member;
import com.karrotclone.utils.RedisUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenProvider {

    public static final long ACCESS_TOKEN_EXPIRE_TIME = 1000L * 60 * 30;
    public static final long REFRESH_TOKEN_EXPIRE_TIME = 1000L * 60 * 60 * 24 * 30;

    final public String ACCESS_TOKEN_NAME = "accessToken";
    final public String REFRESH_TOKEN_NAME = "refreshToken";
    private final RedisUtil redisUtil;
    private final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);
    private String SECRET_KEY = "accesssecretkeyforkarrotclonecodingversionforsecret";

    public JwtTokenProvider(RedisUtil redisUtil){
        this.redisUtil = redisUtil;
    }

    private Key getSigningKey(String secretKey){
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }//시크릿 키를 기반으로 암호화해 설정

    public Claims extracAllClaims(String token) throws ExpiredJwtException {
        return Jwts.parserBuilder().setSigningKey(getSigningKey(SECRET_KEY))
                .build().parseClaimsJws(token).getBody();
    }

    public String getUsername(String token){
        return extracAllClaims(token).get("username", String.class);
    }//Payload 에서 유저 이름을 가져온다.

    public Boolean isTokenExpired(String token){
        final Date expiration = extracAllClaims(token).getExpiration();
        return expiration.before(new Date());
    }//토큰이 만료되었는지 확인

    public String generateAccessToken(Member member){
        return doGenerateToken(member.getUsername(), ACCESS_TOKEN_EXPIRE_TIME);
    }

    public String generateRefreshToken(Member member){
        return doGenerateToken(member.getUsername(), REFRESH_TOKEN_EXPIRE_TIME);
    }

    public String doGenerateToken(String username, long expireTime){
        Claims claims = Jwts.claims();
        claims.put("username", username);

        String jwt = Jwts.builder().setClaims(claims).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expireTime))
                .signWith(getSigningKey(SECRET_KEY), SignatureAlgorithm.HS256)
                .compact();

        return jwt;
    }
    public Long getExpireTime(String token){
        return extracAllClaims(token).getExpiration().getTime();
    }
    public boolean validateToken(String token){
        if(redisUtil.hasKeyBlackList("LOGOUT_"+token)){
            return false;
        }
        return true;
    }
}
