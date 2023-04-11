package com.karrotclone.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.InputStream;

/**
 * FCM 설정을 위한 클래스
 * @lastModified 2023-04-11 노민준
 */
@Component
public class FcmConfig {

    @Value("${fcm.certification}")
    private String credential;

    @PostConstruct
    public void initialize() throws Exception{
        ClassPathResource resource = new ClassPathResource(credential);

        try(InputStream stream = resource.getInputStream()) {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(stream))
                    .build();
        }
    }
}
