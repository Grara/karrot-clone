package com.karrotclone.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

/**
 * 위치의 좌표와 동네명, 유저가 지정한 별칭을 저장하는 클래스입니다.
 * @since 2023-02-22
 * @createdBy 노민준
 * @lastModified 2023-03-03
 */
@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Coordinate {
    private Long latitude; //위도, 세로
    private Long longitude; //경도, 가로
    private String townName; //공식 행정구역 이름 (읍, 면, 동)
    private String alias; //유저가 지정한 이름

    /**
     * 위치정보의 필드에 모두 값이 있는지 검증합니다. <br>
     * null인 필드가 있다면 IllegalArgumentException를 발생
     * @param coordinate 검증할 위치정보
     * @throws IllegalArgumentException null인 필드가 있으면 발생
     * @since 2023-03-03
     */
    public static void validateNotNull(Coordinate coordinate) throws IllegalArgumentException{
        if(coordinate.latitude == null || coordinate.longitude == null || coordinate.townName == null || coordinate.alias == null){
            throw new IllegalArgumentException("거래 선호장소는 완전히 null이거나 모든 필드에 값이 채워져야합니다.");
        }
    }
}
