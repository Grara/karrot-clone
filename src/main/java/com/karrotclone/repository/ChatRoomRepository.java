package com.karrotclone.repository;

import com.karrotclone.domain.ChatRoom;
import com.karrotclone.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    @Query("SELECT c " +
            "FROM ChatRoom c " +
            "WHERE (c.host = :sender AND c.guest = :receiver) " +
            "OR (c.host = :receiver AND c.guest = :sender)")
    Optional<ChatRoom> findByHostAndGuest(Member sender, Member receiver);
}
