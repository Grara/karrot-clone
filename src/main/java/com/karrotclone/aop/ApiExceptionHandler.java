package com.karrotclone.aop;

import com.karrotclone.dto.ResponseDto;
import com.karrotclone.exception.DomainNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.util.NoSuchElementException;

/**
 * RestController에서 발생하는 공통적인 예외들에 대한 핸들링을 설정하는 클래스입니다.
 * @since 2023-03-05
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    
    //Repository에서 조건에 해당하는 엔티티를 1개 찾아야하는데 찾지 못했을 경우
    @ExceptionHandler(DomainNotFoundException.class)
    public ResponseEntity<ResponseDto> handleDomainNotFoundException(DomainNotFoundException e){
        ResponseDto resDto = new ResponseDto();
        resDto.setMessage("조건에 해당하는 엔티티 객체를 찾지 못했습니다. 오류메시지 : " + e.getMessage());
        resDto.setData(null);
        return new ResponseEntity<>(resDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IOException.class) //IO 관련 문제
    public ResponseEntity<ResponseDto> handleIOException(IOException e){
        ResponseDto resDto = new ResponseDto();
        resDto.setMessage("파일 혹은 데이터의 입출력 도중 문제가 발생했습니다. 오류메시지 : " + e.getMessage());
        resDto.setData(null);
        return new ResponseEntity<>(resDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class) //예측 못한 에러
    public ResponseEntity<ResponseDto> handleUnknownException(Exception e){
//        StackTraceElement[] stackTrace = e.getStackTrace();
//        String result = "";
//        for(StackTraceElement s : stackTrace){
//            result += s.toString() + "\n";
//        }

        ResponseDto resDto = new ResponseDto();
        resDto.setMessage("예측하지 못한 오류가 발생했습니다. 오류메시지 : " + e.getMessage());
        resDto.setData(null);
        return new ResponseEntity<>(resDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
