package com.karrotclone.repository;

import com.karrotclone.domain.ChatRoom;
import com.karrotclone.dto.ChatLogDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;


public interface ChatLogQueryRepository {

    Slice<ChatLogDto> findListByChatRoomId(ChatRoom chatRoom, Pageable pageable);
}
