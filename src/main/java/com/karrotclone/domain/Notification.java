package com.karrotclone.domain;

import com.karrotclone.domain.enums.NotificationType;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
public class Notification {
    @Id @GeneratedValue
    @Column(name = "notification_id")
    private Long id;

    private String title; //알림 제목
    private String content; //알림 내용
    private String iconUrl; //아이콘 이미지 url
    private LocalDateTime createDateTime; //생성시간
    @Enumerated(EnumType.STRING)
    private NotificationType notificationType; //타입

    @Builder
    public Notification(String title, String content, String iconUrl, LocalDateTime createDateTime, NotificationType notificationType) {
        this.title = title;
        this.content = content;
        this.iconUrl = iconUrl;
        this.createDateTime = createDateTime;
        this.notificationType = notificationType;
    }
}