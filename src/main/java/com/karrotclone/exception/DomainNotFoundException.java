package com.karrotclone.exception;

/**
 * Repository에서 조건에 해당하는 엔티티를 1개 찾아야하는데 찾지 못했을 경우 발생합니다.
 */
public class DomainNotFoundException extends RuntimeException{

    public DomainNotFoundException(String massage){
        super(massage);
    }

}
