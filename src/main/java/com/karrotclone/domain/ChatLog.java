package com.karrotclone.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = {"chatRoom"})
public class ChatLog {
    @Id @GeneratedValue
    @Column(name = "chatlog_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatroom_id")
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private Member sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    private Member receiver;

    private String message;

    private LocalDateTime createDateTime;

    public ChatLog(Member sender,Member receiver, String message){
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.createDateTime = LocalDateTime.now();
    }
}
