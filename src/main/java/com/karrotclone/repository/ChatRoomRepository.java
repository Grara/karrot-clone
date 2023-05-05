package com.karrotclone.repository;

import com.karrotclone.domain.ChatRoom;
import com.karrotclone.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    @Query("SELECT c " +
           "FROM ChatRoom c " +
           "LEFT JOIN FETCH c.chatLogs " +
           "WHERE (c.host = :sender AND c.guest = :receiver) " +
           "OR (c.host = :receiver AND c.guest = :sender)")
    Optional<ChatRoom> findByHostAndGuest(@Param("sender") Member sender, @Param("receiver")Member receiver);

    @Query("SELECT DISTINCT c " +
           "FROM ChatRoom c " +
           "LEFT JOIN FETCH c.host " +
           "LEFT JOIN FETCH c.guest " +
           "WHERE c.host = :member OR c.guest = :member " +
           "ORDER BY c.lastChatTime DESC")
    List<ChatRoom> findListByMember(@Param("member") Member member);
}
