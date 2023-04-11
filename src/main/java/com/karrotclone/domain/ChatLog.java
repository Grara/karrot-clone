package com.karrotclone.domain;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatLog {
    @Id @GeneratedValue
    @Column(name = "chatlog_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatroom_id")
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private String message;

    private LocalDateTime createDateTime;

    public ChatLog(Member member, String message){
        this.member = member;
        this.message = message;
        this.createDateTime = LocalDateTime.now();
    }
}
