package com.karrotclone.api;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.karrotclone.dto.ImageTestDto;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 * 잡다한 API요청을 처리해주는 컨트롤러입니다.
 * @since 2023-02-22
 * @createdBy 노민준
 */

@RestController
@RequiredArgsConstructor
public class EtcApiController {
    private final StringEncryptor encryptor;
    private final AmazonS3Client amazonS3Client;
    private String bucketName = "helloshop-build";

    /**
     * 문자열을 JASYPT를 이용해 암호화하고 결과를 반환해줍니다
     * @param plainTxt 암호화시킬 문자열
     * @return 암호화된 문자열
     * @since 2023-02-22
     * @lastModified 2023-02-22
     * @createdBy 노민준
     */
    @ApiOperation(value="텍스트 암호화", notes = "일반텍스트를 JASYPT를 이용해 암호화하고 결과를 반환해줍니다.")
    @PostMapping("/api/v1/enc")
    public String encodeToJasypt(String plainTxt){
        return encryptor.encrypt(plainTxt);
    }

    @GetMapping("/api/v1/auth-test") //
    public String authTest(){
        return "Success";
    }
}
