package com.karrotclone.dto;

import com.karrotclone.domain.PreferPlace;
import com.karrotclone.domain.SalesPost;
import com.karrotclone.domain.enums.Category;
import com.karrotclone.domain.enums.SalesState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 거래글 화면 상세페이지에서 필요한 데이터를 나타내는 DTO입니다.
 * @since 2023-02-24
 * @createdBy 노민준
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SalesPostDetailDto {

    private String nickName; //판매자 닉네임
    private String title; //글제목
    private String content; //글내용
    private long price; //가격
    private Category category; //카테고리
    private LocalDateTime createDateTime; //작성시간
    private SalesState salesState; //거래상태
    private boolean isNegoAvailable; //가격제안 가능여부
    private int views; //조회수
    private int favoriteUserCount; //관심수
    private int chatCount; //채팅수
    private PreferPlace preferPlace; //선호장소(선택)
    private List<SalesPostSimpleDto> postsFromSeller; //판매자가 등록한 다른 판매글

    public SalesPostDetailDto(SalesPost post){
        this.nickName = post.getMember().getNickName();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.price = post.getPrice();
        this.category = post.getCategory();
        this.createDateTime = post.getCreateDateTime();
        this.salesState = post.getSalesState();
        this.isNegoAvailable = post.isNegoAvailable();
        this.views = post.getViews();
        this.favoriteUserCount = post.getFavoriteUserCount();
        this.chatCount = post.getChatCount();
        this.preferPlace = post.getPreferPlace();
    }
}
