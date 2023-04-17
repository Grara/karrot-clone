package com.karrotclone.api;


import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.karrotclone.domain.ChatLog;
import com.karrotclone.domain.ChatRoom;
import com.karrotclone.domain.Member;
import com.karrotclone.domain.MemberChatRoomMapping;
import com.karrotclone.dto.ChatMessageDto;
import com.karrotclone.dto.ChatRoomDto;
import com.karrotclone.dto.ResponseDto;
import com.karrotclone.exception.DomainNotFoundException;
import com.karrotclone.repository.ChatMappingRepository;
import com.karrotclone.repository.ChatRoomRepository;
import com.karrotclone.repository.TempMemberRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ChatApiController {
    private final SimpMessageSendingOperations sendingOperations;
    private TempMemberRepository memberRepository;
    private ChatRoomRepository chatRoomRepository;
    private ChatMappingRepository chatMappingRepository;

    /**
     * 현재 로그인한 멤버의 FCM토큰을 참고하여 테스트 푸쉬알림을 보냅니다.
     * @return
     */
    @ApiOperation(value = "FCM테스트용 API", notes = "현재 로그인한 회원의 FCM토큰을 이용해서 테스트 푸쉬알림을 보냅니다.")
    @PostMapping("/api/v1/chat-test")
    public ResponseEntity<ResponseDto> pushTest(@ApiIgnore @AuthenticationPrincipal Member member) throws Exception{

        ResponseDto resDto = new ResponseDto();

        if(member.getFcmToken() == null){
            resDto.setMessage("유저의 FCM토큰이 null입니다");
            return new ResponseEntity<>(resDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        Notification notification = new Notification("테스트입니다.", "테스트에요~");

        Message msg = Message.builder()
                .setNotification(notification)
                .setToken(member.getFcmToken())
                .putData("route", "/")
                .build();

        String response = FirebaseMessaging.getInstance().send(msg);

        resDto.setMessage("메세지 전송에 성공했습니다.");
        return new ResponseEntity<>(resDto, HttpStatus.OK);
    }

    @ApiOperation(value = "나의 채팅목록 불러오기", notes = "나의 채팅목록을 불러옵니다.")
    @GetMapping("/api/v1/members/get-my-chat-list")
    public ResponseEntity<ResponseDto> getMyChatList(@ApiIgnore @AuthenticationPrincipal Member member) {

        List<ChatRoom> chatRooms = chatRoomRepository.findListByMember(member);
        List<ChatRoomDto> chatRoomDtos = new ArrayList<>();

        for(ChatRoom chatRoom : chatRooms){
            ChatRoomDto roomDto = new ChatRoomDto();

            roomDto.setLastMessage(chatRoom.getLastMessage());
            roomDto.setLastChatTime(chatRoom.getLastChatTime());

            if(chatRoom.getHost() != member){ //채팅룸의 호스트가 상대방일 경우
                roomDto.setOtherPersonNickname(chatRoom.getHost().getNickName());
                roomDto.setOtherPersonProfileUrl(chatRoom.getHost().getProfileUrl());
                roomDto.setOtherPersonTownName(chatRoom.getHost().getTown().getTownName());
            }

            else{ //채팅룸의 게스트가 상대방일 경우
                roomDto.setOtherPersonNickname(chatRoom.getGuest().getNickName());
                roomDto.setOtherPersonProfileUrl(chatRoom.getGuest().getProfileUrl());
                roomDto.setOtherPersonTownName(chatRoom.getGuest().getTown().getTownName());
            }

            chatRoomDtos.add(roomDto);
        }

        ResponseDto resDto = new ResponseDto();
        resDto.setData(chatRoomDtos);
        resDto.setMessage("채팅목록 조회에 성공했습니다.");

        return new ResponseEntity<>(resDto, HttpStatus.OK);
    }
}
