package com.bitharmony.comma.global.exception.member;

import com.bitharmony.comma.global.exception.CommaException;

public class NoMoreNotificationException extends CommaException {

    private final static String MESSAGE = "새 알림이 없습니다.";

    public NoMoreNotificationException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 200;
    }
}