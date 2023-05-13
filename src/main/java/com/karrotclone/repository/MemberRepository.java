package com.karrotclone.repository;

import com.karrotclone.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 임시용으로 생성한 멤버 DAO입니다.
 */
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByNickName(String nickname); //멤버 닉네임으로 조회
    Optional<Member> findByEmail(String email);
}
