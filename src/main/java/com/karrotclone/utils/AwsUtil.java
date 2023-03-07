package com.karrotclone.utils;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AwsUtil {

    private final AmazonS3Client amazonS3Client;
    private final String BUCKET_NAME = "helloshop-build";

    /**
     * 파일을 S3에 업로드한 뒤 파일에 접근하는 URL을 반환해줍니다.
     * @param file 업로드할 파일
     * @return 업로드한 파일의 접근 URL
     * @throws IOException
     */
    public String uploadToS3(MultipartFile file) throws IOException {
        String originName = file.getOriginalFilename();
        String uuid = UUID.randomUUID().toString();
        String extention = originName.substring(originName.lastIndexOf(".")); //확장자
        String saveName = uuid + extention;
        long size = file.getSize();

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(file.getContentType());
        objectMetadata.setContentLength(size);

        amazonS3Client.putObject(
                new PutObjectRequest(BUCKET_NAME, saveName, file.getInputStream(), objectMetadata)
                        .withCannedAcl(CannedAccessControlList.PublicRead)
        );

        return amazonS3Client.getUrl(BUCKET_NAME, saveName).toString();
    }

    public void deleteAtS3(String url){

        int idx = url.lastIndexOf("/"); //마지막 "/"의 위치
        String fileName = url.substring(idx + 1); //마지막 "/" 이후가 파일명
        amazonS3Client.deleteObject(BUCKET_NAME, fileName);

    }
}
