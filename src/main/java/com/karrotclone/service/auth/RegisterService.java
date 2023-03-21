package com.karrotclone.service.auth;

import com.karrotclone.domain.Member;
import com.karrotclone.domain.enums.Roles;
import com.karrotclone.dto.RegisterDto;
import com.karrotclone.repository.TempMemberRepository;
import com.karrotclone.utils.AwsUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;

@RequiredArgsConstructor
@Service
@Transactional(rollbackOn = Exception.class)
public class RegisterService {

    private final TempMemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final AwsUtil awsUtil;

    public void register(RegisterDto user) throws IOException {
        if(memberRepository.findByEmail(user.email).isPresent()){
            throw new IllegalArgumentException("Email Already Exist");
        }

        Member member = new Member(user.nickName, user.email, passwordEncoder.encode(user.password), Roles.ROLE_USER, user.town);

        if(user.getProfile() != null){
            String url = awsUtil.uploadToS3(user.getProfile());
            member.setProfileUrl(url);
        }

        memberRepository.save(member);
    }
}
