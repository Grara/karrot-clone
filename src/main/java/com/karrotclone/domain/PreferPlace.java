package com.karrotclone.domain;

import lombok.Data;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;

/**
 * 판매글의 거래 희망 장소를 나타내는 엔티티 객체입니다.
 */
@Embeddable
@Data
public class PreferPlace {
    private int latitude; //위도, 세로
    private int longitude; //경도, 가로
    private String name; //장소명
}
