package com.karrotclone.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatRoomDto {
    private String otherPersonNickname;
    private String otherPersonProfileUrl;
    private String otherPersonTownName;
    private String lastMessage;
    private LocalDateTime lastChatTime;
}
