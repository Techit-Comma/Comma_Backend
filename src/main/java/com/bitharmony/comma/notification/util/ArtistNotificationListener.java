package com.bitharmony.comma.notification.util;

import com.bitharmony.comma.global.exception.streaming.SseEmitterNotFoundException;
import com.bitharmony.comma.global.exception.streaming.SseEmitterSendingException;
import com.bitharmony.comma.notification.repository.SseEmitterRepository;
import com.bitharmony.comma.streaming.util.EncodeStatus;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Component
@RequiredArgsConstructor
public class ArtistNotificationListener implements MessageListener {

    private final SseEmitterRepository sseEmitterRepository;
    private final static Long DEFAULT_TIMEOUT = 60000L * 10;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String messageStr = new String(message.getBody(), StandardCharsets.UTF_8);
        String[] parts = messageStr.split(":");

        String key = parts[0];
        String encodeType = parts[1];
        EncodeStatus status = EncodeStatus.valueOf(parts[2]);

        sendEvent(key, encodeType, status);
    }

    private void sendEvent(String key, Object... event) {
        SseEmitter sseEmitter = sseEmitterRepository.findByKey(key)
                .orElseThrow(SseEmitterNotFoundException::new);
        try {
            sseEmitter.send(SseEmitter.event().reconnectTime(DEFAULT_TIMEOUT)
                    .data(event, MediaType.APPLICATION_JSON)
                    .id(UUID.randomUUID().toString())
                    .name("Notification")
            );

        } catch (IOException e) {
            throw new SseEmitterSendingException();
        }
    }
}
