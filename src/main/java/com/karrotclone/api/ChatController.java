package com.karrotclone.api;


import com.karrotclone.domain.ChatLog;
import com.karrotclone.domain.ChatRoom;
import com.karrotclone.domain.Member;
import com.karrotclone.domain.MemberChatRoomMapping;
import com.karrotclone.dto.ChatMessageDto;
import com.karrotclone.dto.ResponseDto;
import com.karrotclone.exception.DomainNotFoundException;
import com.karrotclone.repository.ChatMappingRepository;
import com.karrotclone.repository.ChatRoomRepository;
import com.karrotclone.repository.TempMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatController {
    private final SimpMessageSendingOperations sendingOperations;
    private TempMemberRepository memberRepository;
    private ChatRoomRepository chatRoomRepository;
    private ChatMappingRepository chatMappingRepository;

    @MessageMapping("/chat/message")
    //@RolesAllowed({"USER"})
    public ResponseEntity<ResponseDto> sendMessage(ChatMessageDto chatMessage, @AuthenticationPrincipal Member member){

        ChatRoom chatRoom = chatRoomRepository.findById(chatMessage.getRoomId())
                .orElseThrow(() -> new DomainNotFoundException("id에 해당하는 채팅방이 없습니다."));
        ChatLog chatLog = new ChatLog(member, chatMessage.getMessage());
        chatRoom.addChatLog(chatLog);
        chatRoomRepository.save(chatRoom);

        sendingOperations.convertAndSend("/topic/room/"+chatMessage.getRoomId(), chatMessage.getMessage());

        ResponseDto resDto = new ResponseDto();
        resDto.setMessage("채팅메세지를 성공적으로 전송했습니다.");
        return new ResponseEntity<>(resDto, HttpStatus.OK);
    }

    @PostMapping("/api/v1/chatroom")
    public ResponseEntity<ResponseDto> createChatRoom(@RequestBody String receiverEmail, @AuthenticationPrincipal Member member){

        ChatRoom chatRoom = chatRoomRepository.save(new ChatRoom());
        MemberChatRoomMapping mapping1 = new MemberChatRoomMapping();
        mapping1.setMember(member);
        mapping1.setChatRoom(chatRoom);
        chatMappingRepository.save(mapping1);

        Member receiver = memberRepository.findByEmail(receiverEmail)
                .orElseThrow(() -> new DomainNotFoundException("이메일에 해당하는 상대 회원이 없습니다."));

        MemberChatRoomMapping mapping2 = new MemberChatRoomMapping();
        mapping2.setMember(receiver);
        mapping2.setChatRoom(chatRoom);
        chatMappingRepository.save(mapping2);

        ResponseDto resDto = new ResponseDto();
        resDto.setMessage("채팅방을 성공적으로 생성했습니다. data는 생성된 채팅방의 id입니다.");
        resDto.setData(chatRoom.getId());

        return new ResponseEntity<>(resDto, HttpStatus.CREATED);
    }
}
