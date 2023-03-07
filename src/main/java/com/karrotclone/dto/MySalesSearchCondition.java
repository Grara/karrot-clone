package com.karrotclone.dto;

import com.karrotclone.domain.enums.SalesState;
import lombok.Data;

/**
 * 내 판매글 목록을 가져올 때 사용하는 검색조건입니다.
 */
@Data
public class MySalesSearchCondition {
    private SalesState salesState;
    private Boolean isHide;
}
