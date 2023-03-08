package com.karrotclone.dto;

import com.karrotclone.domain.enums.SalesState;
import lombok.Data;

/**
 * 지정된 판매자의 판매글의 목록을 가져올 때 사용하는 조건 클래스입니다.
 */

@Data
public class PostSellerSearchCondition {
    private String nickName;
    private SalesState salesState;
}
