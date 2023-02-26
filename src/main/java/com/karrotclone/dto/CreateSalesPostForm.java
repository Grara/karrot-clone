package com.karrotclone.dto;

import com.karrotclone.domain.PreferPlace;
import com.karrotclone.domain.enums.Category;
import com.karrotclone.domain.enums.OpenRange;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 판매글 생성 시 필요한 데이터 폼입니다.
 * @since 2023-02-23
 * @createdBy 노민준
 * @lastModified 2023-02-23
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateSalesPostForm {

    private String title; //제목
    private Category category; //카테고리
    private long price; //가격
    private boolean isNegoAvailable; //가격제안 가능여부
    private String content; //글 내용
    private PreferPlace preferPlace; //거래 희망 장소(선택)
    private OpenRange openRange; //보여줄 동네 범위
}
