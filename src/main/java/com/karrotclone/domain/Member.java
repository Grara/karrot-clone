package com.karrotclone.domain;

import com.karrotclone.domain.enums.Roles;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.List;

/**
 * 회원을 나타내는 엔티티 객체입니다.
 * @since 2023-02-22
 * @createdBy 노민준
 * @lastModified 2023-03-03
 */
@Entity
@Data
@NoArgsConstructor
public class Member implements UserDetails {
    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id; //식별id값
    private String nickName; //닉네임
    private String email; //이메일
    private String password; //비밀번호
    @Enumerated
    private Roles role; //역할

    @Embedded //임베디드타입
    private Coordinate town;

    private long searchRange;

    public Member(String nickName, String email, String password, Roles role, Coordinate town) {
        this.nickName = nickName;
        this.email = email;
        this.password = password;
        this.role = role;
        this.town = town;
        this.searchRange = 20000;
    }

    public Member(Long id, String email, String password, List<GrantedAuthority> authorities, String nickName) {
    }

    @Override //권한정보
    public Collection<? extends GrantedAuthority> getAuthorities() {
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role.toString());
        return List.of(authority);
    }

    @Override
    public String getUsername() {
        return getNickName();
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
