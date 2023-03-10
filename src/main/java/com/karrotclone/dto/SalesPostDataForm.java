package com.karrotclone.dto;

import com.karrotclone.domain.Coordinate;
import com.karrotclone.domain.enums.Category;
import com.karrotclone.domain.enums.RangeStep;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * 판매글 생성 시 필요한 데이터 폼입니다.
 * @since 2023-02-23
 * @createdBy 노민준
 * @lastModified 2023-03-09
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SalesPostDataForm {

    private List<MultipartFile> images = new ArrayList<>(); //이미지 파일들

    @NotNull(message = "제목은 필수입니다")
    private String title; //제목

    @NotNull(message = "카테고리는 필수입니다")
    private Category category; //카테고리

    @NotNull(message = "가격은 필수입니다")
    @Min(value = 0, message = "가격은 0이상이어야 합니다")
    private long price = 0; //가격

    private boolean isNegoAvailable = false; //가격제안 가능여부
    private String content; //글 내용

    @Valid
    private Coordinate preferPlace; //거래 희망 장소 정보

    @NotNull(message = "글 공개범위는 필수입니다")
    private RangeStep rangeStep; //보여줄 동네 범위
}
