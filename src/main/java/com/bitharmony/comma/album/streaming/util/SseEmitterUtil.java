package com.bitharmony.comma.album.streaming.util;

import com.bitharmony.comma.album.streaming.repository.SseEmitterRepository;
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
    private final static Long DEFAULT_TIMEOUT = 60000L * 10;
    private final static Long RECONNECT_TIME = 30000L;
    private final static String EVENT_NAME = "Encoding Status";

    public SseEmitter generateSseEmitter(String filePath) {
        SseEmitter sseEmitter = new SseEmitter(DEFAULT_TIMEOUT);
        sseEmitterRepository.save(filePath, sseEmitter);
        sendEvent(filePath, EVENT_NAME, EncodeStatus.CONNECT);
        return sseEmitter;
    }

    public void sendEvent(String key, Object... event) {
        SseEmitter sseEmitter = sseEmitterRepository.findByKey(key)
                .orElseThrow(SseEmitterNotFoundException::new);

        try {
            sseEmitter.send(SseEmitter.event().reconnectTime(RECONNECT_TIME)
                    .data(event, MediaType.APPLICATION_JSON)
                    .id(UUID.randomUUID().toString())
                    .name(EVENT_NAME)
            );

        } catch (IOException e) {
            throw new SseEmitterSendingException();
        }

        if (isComplete(event)) {
            complete(sseEmitter, key);
        }
    }

    public void complete(SseEmitter sseEmitter, String key) {
        sseEmitter.complete();
        sseEmitterRepository.deleteByKey(key);
    }

    private boolean isComplete(Object... events) {
        for (Object event : events) {
            if (event == EncodeStatus.COMPLETE) {
                return true;
            }
        }

        return false;
    }

}
