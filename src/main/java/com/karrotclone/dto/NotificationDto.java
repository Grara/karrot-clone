package com.karrotclone.dto;

import com.karrotclone.domain.Notification;
import com.karrotclone.domain.enums.NotificationType;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 알림용 DTO클래스
 */

@Data
public class NotificationDto {

    private String title; //알림 제목
    private String content; //알림 내용
    private String iconUrl; //아이콘 이미지 url
    private LocalDateTime createDateTime; //생성시간
    private NotificationType notificationType; //타입

    public NotificationDto(Notification notification) {
        this.title = notification.getTitle();
        this.content = notification.getContent();
        this.iconUrl = notification.getIconUrl();
        this.createDateTime = notification.getCreateDateTime();
        this.notificationType = notification.getNotificationType();
    }
}
