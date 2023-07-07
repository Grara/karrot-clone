package com.karrotclone.utils;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class SseEmitters {
    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    public SseEmitter add(SseEmitter emitter){
        emitters.add(emitter);
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitter.complete());
        return emitter;
    }

    public void sendEvent(String name, Object data) throws IOException {
        for(SseEmitter emitter : emitters){
            emitter.send(SseEmitter.event()
                    .name(name)
                    .data(data));
        }
    }
}
