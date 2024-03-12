package com.bitharmony.comma.member.notification.dto;

import java.time.LocalDateTime;

public record NotificationResponse (
        Long id,
        String message,
        String callerName,
        Boolean isRead,
        LocalDateTime createDate
){

}
