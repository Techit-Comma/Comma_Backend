package com.bitharmony.comma.member.notification.dto;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record NotificationResponse (
        Long notificationId,
        String message,
        String redirectUrl,
        String publisherName,
        String publisherProfileImageUrl,
        Boolean isRead,
        LocalDateTime createDate
){

}
