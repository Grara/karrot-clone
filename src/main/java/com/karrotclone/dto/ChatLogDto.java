package com.karrotclone.dto;

import com.karrotclone.domain.ChatLog;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatLogDto {
    private String senderNickName;
    private String senderProfileUrl;
    private String receiverNickname;
    private String receiverProfileUrl;
    private LocalDateTime createDateTime;
    private String message;

    public ChatLogDto(ChatLog log){
        this.senderNickName = log.getSender().getNickName();
        this.senderProfileUrl = log.getSender().getProfileUrl();
        this.receiverNickname = log.getReceiver().getNickName();
        this.receiverProfileUrl = log.getReceiver().getProfileUrl();
        this.createDateTime = log.getCreateDateTime();
        this.message = log.getMessage();
    }
}
