package com.bitharmony.comma.member.notification.dto;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record NotificationResponse (
        Long id,
        String message,
        String redirectUrl,
        String publisherName,
        Boolean isRead,
        LocalDateTime createDate
){

}
