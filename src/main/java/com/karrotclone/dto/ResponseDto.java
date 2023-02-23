package com.karrotclone.dto;

import lombok.Data;

/**
 * API반환 시 메시지와 데이터를 담을 DTO입니다.
 */
@Data
public class ResponseDto {
    private String message; //프론트측에 전달할 메시지
    private Object data; //전달할 데이터
}
