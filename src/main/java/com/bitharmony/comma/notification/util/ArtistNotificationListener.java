package com.bitharmony.comma.notification.util;

import com.bitharmony.comma.notification.repository.SseEmitterUtil;
import com.bitharmony.comma.streaming.util.EncodeStatus;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ArtistNotificationListener implements MessageListener {

    private final SseEmitterUtil sseEmitterUtil;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String messageStr = new String(message.getBody(), StandardCharsets.UTF_8);
        String[] parts = messageStr.split(":");

        String key = parts[0];
        String encodeType = parts[1];
        EncodeStatus status = EncodeStatus.valueOf(parts[2]);

        sseEmitterUtil.sendEvent(key, encodeType, status);
    }
}
