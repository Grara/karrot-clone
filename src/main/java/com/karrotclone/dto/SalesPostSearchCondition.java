package com.karrotclone.dto;


import com.karrotclone.domain.enums.Category;
import lombok.Data;

/**
 * 거래글 검색 시 조건을 나타내는 클래스입니다.
 * @since 2023-03-03
 * @createdBy 노민준
 */
@Data
public class SalesPostSearchCondition {
    private String title; //제목 검색어
    private Category category; //카테고리
}
