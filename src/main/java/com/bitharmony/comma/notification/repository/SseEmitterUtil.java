package com.bitharmony.comma.notification.repository;

import com.bitharmony.comma.global.exception.streaming.SseEmitterNotFoundException;
import com.bitharmony.comma.global.exception.streaming.SseEmitterSendingException;
import java.io.IOException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Component
@RequiredArgsConstructor
public class SseEmitterUtil {

    private final SseEmitterRepository sseEmitterRepository;
    private final static Long RECONNECT_TIME = 30000L;

    public void sendEvent(String key, Object... event) {
        SseEmitter sseEmitter = sseEmitterRepository.findByKey(key)
                .orElseThrow(SseEmitterNotFoundException::new);

        try {
            sseEmitter.send(SseEmitter.event().reconnectTime(RECONNECT_TIME)
                    .data(event, MediaType.APPLICATION_JSON)
                    .id(UUID.randomUUID().toString())
                    .name("Notification")
            );

        } catch (IOException e) {
            throw new SseEmitterSendingException();
        }
    }

    public void complete(SseEmitter sseEmitter, String key) {
        sseEmitter.complete();
        sseEmitterRepository.deleteByKey(key);
    }

}
