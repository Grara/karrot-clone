package com.karrotclone.dto;

import lombok.Data;

@Data
public class ChatMessageDto {
    private String senderEmail;
    private String receiverEmail;
    private String message;
}
