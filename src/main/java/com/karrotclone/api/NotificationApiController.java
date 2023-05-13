package com.karrotclone.api;

import com.karrotclone.dto.ResponseDto;
import com.karrotclone.utils.SseEmitters;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class NotificationApiController {

    private final SseEmitters sseEmitters;

    @GetMapping(value = "/api/v1/notification/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<ResponseDto> connect() throws Exception {
        SseEmitter emitter = new SseEmitter(60 * 1000L);
        sseEmitters.add(emitter);
        emitter.send(SseEmitter.event()
                .name("connect")
                .data("connected!"));
        ResponseDto resDto = new ResponseDto();
        resDto.setMessage("연결이 정상적으로 완료되었습니다.");
        resDto.setData(emitter);
        return new ResponseEntity<>(resDto, HttpStatus.OK);
    }
}