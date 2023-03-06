package com.karrotclone.aop;

import com.karrotclone.dto.ResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class ApiExceptionHandler {

    /**
     *
     * @param e
     * @return
     */
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ResponseDto> handleNoSuchElementException(NoSuchElementException e){
        ResponseDto dto = new ResponseDto();
        dto.setMessage("조건에 해당하는 엔티티 객체를 찾지 못했습니다. // 오류메시지 : " + e.getMessage());
        dto.setData(e);
        return new ResponseEntity<>(dto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<ResponseDto> handleIOException(IOException e){
        ResponseDto dto = new ResponseDto();
        dto.setMessage("파일 혹은 데이터의 입출력 도중 문제가 발생했습니다. // 오류메시지 : " + e.getMessage());
        dto.setData(e);
        return new ResponseEntity<>(dto, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDto> handleUnknownException(Exception e){
        ResponseDto dto = new ResponseDto();
        dto.setMessage("예측하지 못한 오류가 발생했습니다. // 오류메시지 : " + e.getMessage());
        dto.setData(e);
        return new ResponseEntity<>(dto, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
