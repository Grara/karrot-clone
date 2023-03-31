package com.karrotclone.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class ChatRoom {
    @Id @GeneratedValue
    @Column(name = "chatroom_id")
    private Long id;

    @OneToMany(mappedBy = "chatRoom")
    private List<MemberChatRoomMapping> chatMappings = new ArrayList<>();

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL)
    private List<ChatLog> chatLogs = new ArrayList<>();

    public void addChatLog(ChatLog log){
        getChatLogs().add(log);
        log.setChatRoom(this);
    }
}
