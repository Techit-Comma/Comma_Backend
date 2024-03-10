package com.bitharmony.comma.notification.dto;

import java.time.LocalDateTime;

public record NotificationResponse (
        Long id,
        String message,
        String callerName,
        Boolean isRead,
        LocalDateTime createDate
){

}
