package com.karrotclone.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
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

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.PERSIST)
    private List<ChatLog> chatLogs = new ArrayList<>();

    public void addChatLog(ChatLog log){
        getChatLogs().add(log);
        log.setChatRoom(this);
    }

    public ChatRoom(Member host, Member guest) {
        this.host = host;
        this.guest = guest;
    }
}
