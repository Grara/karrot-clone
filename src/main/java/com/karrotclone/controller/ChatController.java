package com.karrotclone.controller;

import com.karrotclone.domain.ChatLog;
import com.karrotclone.domain.ChatRoom;
import com.karrotclone.domain.Member;
import com.karrotclone.dto.ChatMessageDto;
import com.karrotclone.dto.ResponseDto;
import com.karrotclone.exception.DomainNotFoundException;
import com.karrotclone.repository.ChatLogRepository;
import com.karrotclone.repository.ChatRoomRepository;
import com.karrotclone.repository.TempMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessageSendingOperations sendingOperations;
    private final ChatRoomRepository chatRoomRepository;
    private final TempMemberRepository memberRepository;
    private final ChatLogRepository chatLogRepository;
    private Map<String, String> userSessions = new ConcurrentHashMap<>();

    /**
     * 웹소켓을 이용한 채팅메세지 전달
     * @param receiverEmail 수신자 이메일
     * @param message 메세지DTO
     * @lastModified 2023-04-15 노민준
     */
    @MessageMapping("/chat/{senderEmail}/{receiverEmail}")
    //@RolesAllowed({"USER"})
    public void sendMessage(@DestinationVariable("senderEmail") String senderEmail, @DestinationVariable("receiverEmail") String receiverEmail, String message){

        Member sender = memberRepository.findByEmail(senderEmail)
                .orElseThrow(() -> new DomainNotFoundException("존재하지 않는 멤버입니다."));
        Member receiver = memberRepository.findByEmail(receiverEmail)
                .orElseThrow(() -> new DomainNotFoundException("존재하지 않는 멤버입니다."));

        Optional<ChatRoom> _chatRoom = chatRoomRepository.findByHostAndGuest(sender, receiver);

        ChatRoom chatRoom = _chatRoom.orElse(new ChatRoom(sender, receiver));

        ChatLog log = new ChatLog(sender, receiver, message);

        chatRoom.addChatLog(log);
        chatRoom.setLastMessage(message);
        chatRoomRepository.save(chatRoom); //채팅 로그부터 저장하면 에러발생함
        chatLogRepository.save(log);

        sendingOperations.convertAndSend("/queue/messages/" + senderEmail + "/" + receiverEmail, message);

    }

    /**
     * 웹소켓 연결 이벤트
     */
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        GenericMessage m1 = (GenericMessage) accessor.getHeader("simpConnectMessage");
        List<String> _email = (List) m1.getHeaders().get("nativeHeaders", Map.class).get("email");
        String email = _email.get(0);
        if (email != null) {
            userSessions.put(email, email);
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
