package com.karrotclone.dto;

import com.karrotclone.domain.Coordinate;
import com.karrotclone.domain.enums.Category;
import com.karrotclone.domain.enums.RangeStep;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 * 판매글 생성 시 필요한 데이터 폼입니다.
 * @since 2023-02-23
 * @createdBy 노민준
 * @lastModified 2023-03-03
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateSalesPostForm {

    private List<MultipartFile> images = new ArrayList<>(); //이미지 파일들
    private String title; //제목
    private Category category; //카테고리
    private long price; //가격
    private boolean isNegoAvailable; //가격제안 가능여부
    private String content; //글 내용
    private Coordinate preferPlace; //거래 희망 장소 정보
    private RangeStep rangeStep; //보여줄 동네 범위
}
