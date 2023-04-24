package com.karrotclone.repository;

import com.karrotclone.domain.ChatLog;
import com.karrotclone.domain.ChatRoom;
import com.karrotclone.domain.QChatLog;
import com.karrotclone.domain.QMember;
import com.karrotclone.dto.ChatLogDto;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.stream.Collectors;

import static com.karrotclone.domain.QChatLog.chatLog;

/**
 * 채팅기록을 가져오기위해 복잡한 쿼리를 작성하는 클래스입니다.
 */
public class ChatLogQueryRepositoryImpl implements ChatLogQueryRepository{

    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    public ChatLogQueryRepositoryImpl(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    /**
     * DB에서 채팅방의 채팅기록을 가져옵니다.
     * @param chatRoom 채팅기록을 가져올 채팅방
     * @param pageable 페이징 정보
     * @return 채팅기록DTO 리스트와 페이징 정보
     */
    @Override
    public Slice<ChatLogDto> findListByChatRoomId(ChatRoom chatRoom, Pageable pageable) {
        List<ChatLog> _content = queryFactory
                .select(chatLog)
                .from(chatLog)
                .leftJoin(chatLog.sender, QMember.member).fetchJoin()
                .leftJoin(chatLog.receiver, QMember.member).fetchJoin()
                .where(chatLog.chatRoom.eq(chatRoom))
                .orderBy(chatLog.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<ChatLogDto> content =
                _content.stream().map(ChatLogDto::new).collect(Collectors.toList());

        return new SliceImpl<>(content, pageable, content.size() == pageable.getPageSize());

    }
}
