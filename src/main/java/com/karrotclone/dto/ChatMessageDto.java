package com.karrotclone.dto;

import lombok.Data;

@Data
public class ChatMessageDto {
    private String receiverEmail;
    private Long roomId;
    private String message;
}
