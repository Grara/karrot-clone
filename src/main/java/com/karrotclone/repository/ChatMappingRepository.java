package com.karrotclone.repository;

import com.karrotclone.domain.MemberChatRoomMapping;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMappingRepository extends JpaRepository<MemberChatRoomMapping, Long> {
}
