package com.example.board.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final ConcurrentHashMap<Integer, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final static Long DEFAULT_TIMEOUT = 3600000L;

    public SseEmitter connectNotification(int userId) {
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);
        this.emitters.put(userId, emitter);

        emitter.onCompletion(() -> this.emitters.remove(emitter));
        emitter.onTimeout(() -> {
            emitter.complete();
            this.emitters.remove(emitter);
        });

        return emitter;
    }

    public void dispatch(int userId, String message) {
        SseEmitter emitter = emitters.get(userId);
        if(emitter != null) {
            try {
                emitter.send(SseEmitter.event().data(message));
            } catch (IOException e) {
                emitter.completeWithError(e);
                emitters.remove(userId);
            }
        }
    }

}
