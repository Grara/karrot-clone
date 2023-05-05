package com.karrotclone.domain;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class ChatRoom {
    @Id @GeneratedValue
    @Column(name = "chatroom_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "host_id")
    private Member host; //채팅방의 생성자(채팅을 맨 처음 먼저 시작한 사람)

    @ManyToOne
    @JoinColumn(name = "guest_id")
    private Member guest; //호스트가 아닌 채팅 참여자

    @OneToMany(mappedBy = "chatRoom")
    private List<ChatLog> chatLogs = new ArrayList<>();

    private String lastMessage; //마지막 메세지
    @Setter(AccessLevel.NONE)
    private LocalDateTime lastChatTime; //마지막 메세지를 보낸 시간

    public void addChatLog(ChatLog log){
        getChatLogs().add(log);
        log.setChatRoom(this);
    }

    public ChatRoom(Member host, Member guest) {
        this.host = host;
        this.guest = guest;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
        this.lastChatTime = LocalDateTime.now();
    }
}
