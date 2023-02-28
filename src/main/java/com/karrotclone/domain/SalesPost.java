package com.karrotclone.domain;

import com.karrotclone.domain.enums.Category;
import com.karrotclone.domain.enums.OpenRange;
import com.karrotclone.domain.enums.SalesState;
import com.karrotclone.dto.CreateSalesPostForm;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 판매글을 나타내는 엔티티 객체입니다.
 * @since 2023-02-22
 * @createdBy 노민준
 */
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SalesPost {

    @Id @GeneratedValue
    @Column(name = "sales_post_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) //단방향 다대일
    @JoinColumn(name = "member_id")
    private Member member; //작성자

    @ElementCollection(fetch = FetchType.LAZY)
    @Column(name = "image_url")
    private List<String> imageUrls = new ArrayList<>(); //상품 이미지 url

    private String title; //글 제목
    private long price; //가격
    private String content; //글 내용
    @Enumerated(value = EnumType.STRING)
    private Category category; //카테고리
    private LocalDateTime createDateTime; //작성일자
    private PreferPlace preferPlace; //거래 희망 장소(선택)
    private boolean isNegoAvailable; //가격제안 가능 여부
    @Enumerated(value = EnumType.STRING)
    private OpenRange openRange; //보여줄 동네 범위
    private int views; //조회수
    @Enumerated(value = EnumType.STRING)
    private SalesState salesState; //판매상태
    private int favoriteUserCount; //관심표시를 한 유저
    private int chatCount; //이 거래로 인해 생성된 채팅 수

    //거래글 생성 데이터 폼과 멤버를 바탕으로 한 생성자
    public SalesPost(CreateSalesPostForm form, Member member){
        this.member = member;
        this.title = form.getTitle();
        this.category = form.getCategory();
        this.price = form.getPrice();
        this.isNegoAvailable = form.isNegoAvailable();
        this.content = form.getContent();
        this.preferPlace = form.getPreferPlace();
        this.openRange = form.getOpenRange();

        this.salesState = SalesState.DEFAULT;
        this.createDateTime = LocalDateTime.now();
    }

}
