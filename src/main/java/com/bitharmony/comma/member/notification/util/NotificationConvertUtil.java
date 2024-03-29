package com.bitharmony.comma.member.notification.util;

import com.bitharmony.comma.member.notification.dto.NotificationResponse;
import com.bitharmony.comma.member.notification.entity.Notification;
import org.springframework.stereotype.Component;

@Component
public class NotificationConvertUtil {

    public NotificationResponse convertToNotificationResponse(Notification notification) {
        return NotificationResponse.builder()
                .notificationId(notification.getId())
                .message(notification.getMessage())
                .redirectUrl(notification.getRedirectUrl())
                .publisherName(notification.getPublisher().getNickname())
                .publisherProfileImageUrl(notification.getPublisher().getImageUrl())
                .isRead(notification.getIsRead())
                .createDate(notification.getCreateDate())
                .build();
    }


}
