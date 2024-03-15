package com.bitharmony.comma.global.exception.member;

import com.bitharmony.comma.global.exception.CommaException;

public class NotificationNotFoundException extends CommaException {

    private final static String MESSAGE = "찾으시는 알림이 없습니다.";

    public NotificationNotFoundException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 400;
    }
}