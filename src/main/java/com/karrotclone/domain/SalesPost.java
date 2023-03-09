package com.karrotclone.domain;

import com.karrotclone.domain.enums.Category;
import com.karrotclone.domain.enums.SalesState;
import com.karrotclone.dto.SalesPostDataForm;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
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

    @Setter(AccessLevel.NONE)
    private LocalDateTime createDateTime; //작성일자

    private Coordinate tradePlace; //거래 장소
    private boolean hasPreferPlace; //거래 희망 장소를 지정했는지?
    private boolean isNegoAvailable; //가격제안 가능 여부
    private boolean isHide; //숨기기 여부
    private long openRange; //보여줄 동네 범위

    @Setter(AccessLevel.NONE)
    private int views; //조회수

    @Enumerated(value = EnumType.STRING)
    private SalesState salesState; //판매상태

    @Setter(AccessLevel.NONE)
    private int favoriteUserCount; //관심표시를 한 유저

    @Setter(AccessLevel.NONE)
    private int chatCount; //이 거래로 인해 생성된 채팅 수


    @PrePersist //영속화 되기전에 실행할 로직
    public void setting(){
        /*
        이미지가없을 경우 url을 "없음"으로 추가
        굳이 넣는 이유는 판매자의 다른글 불러오기할 때 N+1을 막기위해 이미지 url을 페치조인하는데
        이미지url이 아무것도 없으면 거래글 조회가 안됨
        */
        if(getImageUrls() == null)
            setImageUrls(new ArrayList<>());
    }

    /**
     * 폼 데이터와 판매자의 정보를 바탕으로 거래글 객체를 생성합니다.
     * @param form 폼 데이터
     * @param member 판매자
     * @return 생성된 거래글 객체
     * @since 2023-03-04
     */
    public static SalesPost createByFormAndMember(SalesPostDataForm form, Member member){
        SalesPost post = SalesPost.builder()
                .member(member)
                .title(form.getTitle())
                .content(form.getContent())
                .category(form.getCategory())
                .price(form.getPrice())
                .isNegoAvailable(form.isNegoAvailable())
                .isHide(false)
                .salesState(SalesState.DEFAULT)
                .createDateTime(LocalDateTime.now())
                .openRange(form.getRangeStep().getDistance()) //공개 범위 설정
                .build();

        post.setImageUrls(new ArrayList<>());

        if(form.getPreferPlace() != null){ //생성요청폼에 거래선호 장소를 지정한 경우
            post.setTradePlace(form.getPreferPlace());
            post.setHasPreferPlace(true);
        }else{ //안했을 경우 회원의 기본 동네위치로 설정
            post.setTradePlace(member.getTown());
            post.setHasPreferPlace(false);
        }

        return post;
    }

    /**
     * 폼 데이터를 바탕으로 거래글을 수정합니다.
     * @param form 폼 데이터
     * @return 생성된 거래글 객체
     * @since 2023-03-06
     */
    public void updateByForm(SalesPostDataForm form){
        setTitle(form.getTitle());
        setContent(form.getContent());
        setCategory(form.getCategory());
        setPrice(form.getPrice());
        setNegoAvailable(form.isNegoAvailable());
        setOpenRange(form.getRangeStep().getDistance());

        if(form.getPreferPlace() != null){ //생성요청폼에 거래선호 장소를 지정한 경우
            setTradePlace(form.getPreferPlace());
            setHasPreferPlace(true);
        }else{ //안했을 경우 회원의 기본 동네위치로 설정
            setTradePlace(member.getTown());
            setHasPreferPlace(false);
        }
    }


    public void addViews(){ //조회수 추가
        this.views++;
    }

    public void addFavoriteCount(){ //관심 수 추가
        this.favoriteUserCount++;
    }

    public void addChatCount(){ //채팅 수 추가
        this.chatCount++;
    }

    public void reduceViews() { this.views--; }

    public void reduceFavoriteCount(){ //관심 수 추가
        this.favoriteUserCount--;
    }

    public void reduceChatCount(){ //채팅 수 추가
        this.chatCount--;
    }

}
