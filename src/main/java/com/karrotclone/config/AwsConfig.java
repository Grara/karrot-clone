package com.karrotclone.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * AWS를 사용하기 위해서 빈 객체를 등록하는 클래스입니다.
 */
@Configuration
public class AwsConfig {

    @Value("${cloud.aws.iam.access-key}")
    private String accessKey;

    @Value("${cloud.aws.iam.secret-key}")
    private String secretKey;

    private String region = "ap-northeast-2";

    @Bean
    public AmazonS3Client amazonS3Client(){
        BasicAWSCredentials basicAWSCredentials = new BasicAWSCredentials(accessKey, secretKey);
        return (AmazonS3Client) AmazonS3ClientBuilder.standard()
                .withRegion(region)
                .withCredentials(new AWSStaticCredentialsProvider(basicAWSCredentials))
                .build();
    }

}
