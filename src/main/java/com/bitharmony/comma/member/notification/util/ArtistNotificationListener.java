package com.bitharmony.comma.member.notification.util;

import com.bitharmony.comma.member.notification.repository.CompletableFutureRepository;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ArtistNotificationListener implements MessageListener {

    private final CompletableFutureRepository completableFutureRepository;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String messageStr = new String(message.getBody(), StandardCharsets.UTF_8);
        String[] parts = messageStr.split(":");

        String key = parts[0];
        String notificationId = parts[1];
        String notificationMessage = parts[2];

        completeFuture(key, notificationId + "_" + notificationMessage);
    }

    public void completeFuture(String key, String message) {
        completableFutureRepository.findByKey(key).ifPresent(future -> {
            future.complete(message);
            completableFutureRepository.deleteAllByKey(key);
        });
    }
}
