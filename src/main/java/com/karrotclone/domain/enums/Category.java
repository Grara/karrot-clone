package com.karrotclone.domain.enums;

import java.awt.print.Book;

/**
 * 판매글의 상품 카테고리를 나타내는 enum입니다.
 * @since 2023-02-22
 * @createdBy 노민준
 */
public enum Category {
    Digital, //디지털기기
    Appliance, //생활가전
    Interior, //가구, 인테리어
    HomeAndKitchen, //생활, 주방
    KidProduct, //유아제품
    KidBook, //유아도서
    WomenClothes, //여성의류
    WomenAccessory, //여성잡화
    MenFashion, //남성패션
    Beauty, //뷰티, 미용
    Sports, //스포츠
    Hobby, //취미, 게임, 음반
    Car, //중고차
    Book, //도서
    Ticket, //티켓, 교환권
    Food, //가공식품
    Pet, //반려동물 용품
    Plant, //식물
    Etc, //기타

    //private String value;

    Category(){ }

//    Category(String value){
//        this.value = value;
//    }


}
