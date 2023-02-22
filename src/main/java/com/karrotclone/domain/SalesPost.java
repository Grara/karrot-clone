package com.karrotclone.domain;

import com.karrotclone.domain.enums.Category;
import com.karrotclone.domain.enums.SalesState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 판매글을 나타내는 엔티티 객체입니다.
 * @since 2023-02-22
 * @createdBy 노민준
 */
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SalesPost {

    @Id @GeneratedValue
    @Column(name = "sales_post_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) //단방향 다대일
    @JoinColumn(name = "member_id")
    private Member member;

    private long price; //가격
    private String title; //글 제목
    private String content; //글 내용
    private Category category; //카테고리
    private LocalDateTime createDateTime; //작성일자
    private Address preferPlace; //거래 희망 장소, nullable
    private int views; //조회수
    private SalesState salesState; //판매상태
    private int favoriteUserCount; //관심표시를 한 유저
    private int chatCount; //이 거래로 인해 생성된 채팅 수

}
