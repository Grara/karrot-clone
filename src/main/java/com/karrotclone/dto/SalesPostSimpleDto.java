package com.karrotclone.dto;

import com.karrotclone.domain.Address;
import com.karrotclone.domain.SalesPost;
import com.karrotclone.domain.enums.SalesState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 거래글 목록 화면에서 각 상품의 정보를 나타내는 DTO입니다.
 * @since 2023-02-24
 * @createdBy 노민준
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SalesPostSimpleDto {

    private long id; //거래글 id
    private String title; //글제목
    private long price; //가격
    private LocalDateTime createDateTime; //작성시간
    private Address address; //주소
    private SalesState salesState; //거래상태
    private int favoriteUserCount; //관심수
    private int chatCount; //채팅수

    public SalesPostSimpleDto(SalesPost post){
        this.id = post.getId();
        this.title = post.getTitle();
        this.price = post.getPrice();
        this.createDateTime = post.getCreateDateTime();
        this.address = post.getMember().getAddress();
        this.salesState = post.getSalesState();
        this.favoriteUserCount = post.getFavoriteUserCount();
        this.chatCount = post.getChatCount();
    }
}
