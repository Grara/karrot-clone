package com.karrotclone.service.auth;

import com.karrotclone.domain.Member;
import com.karrotclone.domain.enums.Roles;
import com.karrotclone.dto.RegisterDto;
import com.karrotclone.repository.TempMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(rollbackOn = Exception.class)
public class RegisterService {

    private final TempMemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public void register(RegisterDto user){
        if(memberRepository.findByEmail(user.email).isPresent()){
            throw new IllegalArgumentException("Email Already Exist");
        }
        Member member = new Member(user.nickName, user.email, passwordEncoder.encode(user.password), Roles.USER, user.town);
        memberRepository.save(member);
    }
}
