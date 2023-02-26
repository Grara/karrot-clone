package com.karrotclone.domain.enums;

import java.awt.print.Book;

/**
 * 판매글의 상품 카테고리를 나타내는 enum입니다.
 * @since 2023-02-22
 * @createdBy 노민준
 */
public enum Category {
    DIGITAL, //디지털기기
    APPLIANCE, //생활가전
    INTERIOR, //가구, 인테리어
    HOME_KITCHEN, //생활, 주방
    KID_PRODUCT, //유아제품
    KID_BOOK, //유아도서
    WOMEN_CLOTHES, //여성의류
    WOMEN_ACCESSORY, //여성잡화
    MEN_FASHION, //남성패션
    BEAUTY, //뷰티, 미용
    SPORTS, //스포츠
    HOBBY, //취미, 게임, 음반
    CAR, //중고차
    BOOK, //도서
    TICKET, //티켓, 교환권
    FOOD, //가공식품
    PET, //반려동물 용품
    PLANT, //식물
    ETC, //기타

    //private String value;

    Category(){ }

//    Category(String value){
//        this.value = value;
//    }


}
