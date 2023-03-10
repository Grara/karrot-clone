package com.karrotclone.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

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
    @NotNull(message = "위치정보는 null이거나, 모든 필드의 값이 채워져야합니다. 현재 위도는 null입니다")
    private Long latitude; //위도, 세로
    @NotNull(message = "위치정보는 null이거나, 모든 필드의 값이 채워져야합니다. 현재 경도는 null입니다")
    private Long longitude; //경도, 가로
    @NotNull(message = "위치정보는 null이거나, 모든 필드의 값이 채워져야합니다. 현재 행정구역명은 null입니다")
    private String townName; //공식 행정구역 이름 (읍, 면, 동)
    @NotNull(message = "위치정보는 null이거나, 모든 필드의 값이 채워져야합니다. 현재 장소명은 null입니다")
    private String alias; //유저가 지정한 이름
}
