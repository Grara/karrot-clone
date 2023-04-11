package com.karrotclone.domain;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class MemberChatRoomMapping {
    @Id @GeneratedValue
    @Column(name = "member_chatroom_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="chatroom_id")
    private ChatRoom chatRoom;
}
