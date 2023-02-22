package com.karrotclone.api;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 잡다한 API요청을 처리해주는 컨트롤러입니다.
 * @since 2023-02-22
 * @createdBy 노민준
 */

@RestController
@RequiredArgsConstructor
public class EtcApiController {
    private final StringEncryptor encryptor;

    /**
     * 문자열을 JASYPT를 이용해 암호화하고 결과를 반환해줍니다
     * @param plainTxt 암호화시킬 문자열
     * @return 암호화된 문자열
     * @since 2023-02-22
     * @lastModified 2023-02-22
     * @createdBy 노민준
     */
    @ApiOperation(value="텍스트 암호화", notes = "일반텍스트를 JASYPT를 이용해 암호화하고 결과를 반환해줍니다.")
    @PostMapping("/api/enc")
    public String encodeToJasypt(String plainTxt){
        return encryptor.encrypt(plainTxt);
    }
}
