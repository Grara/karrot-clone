package com.karrotclone;

import com.karrotclone.domain.Address;
import com.karrotclone.domain.Coordinate;
import com.karrotclone.domain.Member;
import com.karrotclone.domain.SalesPost;
import com.karrotclone.domain.enums.Roles;
import com.karrotclone.repository.SalesPostRepository;
import com.karrotclone.repository.TempMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * 애플리케이션 시작 시 지정한 로직을 수행하는 임시 러너입니다.
 */

@Component
@RequiredArgsConstructor
public class TempInitRunner implements ApplicationRunner {

    private final TempMemberRepository tempMemberRepository;
    private final SalesPostRepository salesPostRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Member member = new Member("user", "ddd", "1234", Roles.ROLE_USER, new Coordinate(0L,0L,"망원동", "망원역"));
        tempMemberRepository.save(member); //임시 멤버 생성


    }
}
