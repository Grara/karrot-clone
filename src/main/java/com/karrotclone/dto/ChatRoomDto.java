package com.karrotclone.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatRoomDto {
    private String chatMateNickname;
    private String chatMateProfileUrl;
    private String chatMateTownName;
    private String lastMessage;
    private LocalDateTime lastChatTime;
    private Long chatroomId;
}
