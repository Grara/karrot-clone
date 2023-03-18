package com.karrotclone.service.auth;

import com.karrotclone.domain.Member;
import com.karrotclone.repository.TempMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final TempMemberRepository memberRepository;

    /**
     * 이메일로 DB에서 회원을 찾고 해당 회원(UserDetails)를 반환합니다.
     * @param email 찾을 회원의 이메일
     * @return 찾아낸 회원
     * @throws UsernameNotFoundException 회원이 DB에 없을 경우
     * @lastModified 2023-03-18 노민준
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<Member> _user = memberRepository.findByEmail(email);
        if(_user.isEmpty()){
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
        }
        return _user.get();
    }
}
