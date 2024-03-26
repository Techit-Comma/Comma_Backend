package com.bitharmony.comma.member.notification.util;

import com.bitharmony.comma.member.notification.repository.DeferredResultRepository;
import com.bitharmony.comma.member.notification.repository.NotificationRepository;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ArtistNotificationListener implements MessageListener {

    private final DeferredResultRepository deferredResultRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationConvertUtil notificationConvertUtil;

    @Override
    @Transactional(readOnly = true)
    public void onMessage(Message message, byte[] pattern) {
        String messageStr = new String(message.getBody(), StandardCharsets.UTF_8);
        String[] parts = messageStr.split(":");

        String key = parts[0];
        // String notificationId = parts[1];
        // String notificationMessage = parts[2];

        completeDeferredResult(key);
    }


    public void completeDeferredResult(String key) {
        deferredResultRepository.findByKey(key).ifPresent(deferredResult -> {
            deferredResult.setResult(
                    notificationRepository.findAllBySubscriberIdOrderByCreateDateDesc(Long.parseLong(key)).stream()
                            .map(notificationConvertUtil::convertToNotificationResponse)
                            .toList()
            );
            deferredResultRepository.deleteByKey(key);
        });
    }


}
