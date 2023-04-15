package com.karrotclone.controller;

import com.karrotclone.domain.ChatLog;
import com.karrotclone.domain.ChatRoom;
import com.karrotclone.domain.Member;
import com.karrotclone.dto.ChatMessageDto;
import com.karrotclone.dto.ResponseDto;
import com.karrotclone.exception.DomainNotFoundException;
import com.karrotclone.repository.ChatRoomRepository;
import com.karrotclone.repository.TempMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessageSendingOperations sendingOperations;
    private final ChatRoomRepository chatRoomRepository;
    private final TempMemberRepository memberRepository;
    private Map<String, String> userSessions = new ConcurrentHashMap<>();

    /**
     * 웹소켓을 이용한 채팅메세지 전달
     * @param receiverEmail 수신자 이메일
     * @param message 메세지DTO
     * @lastModified 2023-04-15 노민준
     */
    @MessageMapping("/chat/{receiverEmail}")
    //@RolesAllowed({"USER"})
    public void sendMessage(@DestinationVariable String receiverEmail, ChatMessageDto message){

        Member sender = memberRepository.findByEmail(message.getSenderEmail())
                .orElseThrow(() -> new DomainNotFoundException("존재하지 않는 멤버입니다."));
        Member receiver = memberRepository.findByEmail(receiverEmail)
                .orElseThrow(() -> new DomainNotFoundException("존재하지 않는 멤버입니다."));

        ChatRoom chatRoom = chatRoomRepository.findByHostAndGuest(sender, receiver)
                .orElse(chatRoomRepository.save(new ChatRoom(sender, receiver)));

        chatRoom.addChatLog(new ChatLog(sender, receiver, message.getMessage()));
        chatRoomRepository.save(chatRoom);

        String sessionId = userSessions.get(receiverEmail);
        if (sessionId != null) {
            sendingOperations.convertAndSendToUser(sessionId, "/queue/messages", message);
        }
    }

    /**
     * 웹소켓 연결 이벤트
     */
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String email = accessor.getFirstNativeHeader("email");
        if (email != null) {
            userSessions.put(email, accessor.getSessionId());
        }
    }

    /**
     * 웹소켓 연결 해제 이벤트
     */
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String email = accessor.getFirstNativeHeader("email");
        if (email != null) {
            userSessions.remove(email);
        }
    }

}
