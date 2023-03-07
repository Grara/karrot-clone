package com.karrotclone.service.auth;

import com.karrotclone.domain.Member;
import com.karrotclone.repository.TempMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Service
@Transactional(rollbackOn = Exception.class)
public class LoginService {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    private final TempMemberRepository memberRepository;

    public Member login(String email, String password){
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(email, password);

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new NoSuchElementException("유저가 없습니다."));
        if(BCrypt.checkpw(password, member.getPassword())){
            return member;
        }else{
            throw new RuntimeException("비밀번호가 일치하지 않습니다");
        }
    }
}
