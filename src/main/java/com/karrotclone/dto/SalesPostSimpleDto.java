package com.karrotclone.dto;

import com.karrotclone.domain.Address;
import com.karrotclone.domain.SalesPost;
import com.karrotclone.domain.enums.SalesState;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 거래글 목록 화면에서 각 상품의 정보를 나타내는 DTO입니다.
 * @since 2023-02-24
 * @createdBy 노민준
 * @lastModified 2023-03-03
 */
@Data
@NoArgsConstructor
public class SalesPostSimpleDto {

    private String imageUrl; //썸네일 이미지 url
    private long id; //거래글 id
    private String title; //글제목
    private long price; //가격
    private LocalDateTime createDateTime; //작성시간
    private String townName; //표시할 동네명
    private SalesState salesState; //거래상태
    private int favoriteUserCount; //관심수
    private int chatCount; //채팅수

    @QueryProjection
    public SalesPostSimpleDto(SalesPost post){
        if(!post.getImageUrls().isEmpty()){
            this.imageUrl = post.getImageUrls().get(0);
        }
        this.id = post.getId();
        this.title = post.getTitle();
        this.price = post.getPrice();
        this.createDateTime = post.getCreateDateTime();
        this.salesState = post.getSalesState();
        this.favoriteUserCount = post.getFavoriteUserCount();
        this.chatCount = post.getChatCount();
        this.townName = post.getTradePlace().getTownName();
    }
}
