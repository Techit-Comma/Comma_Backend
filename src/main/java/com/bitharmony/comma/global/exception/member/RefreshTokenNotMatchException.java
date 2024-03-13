package com.bitharmony.comma.global.exception.member;

import com.bitharmony.comma.global.exception.CommaException;

public class RefreshTokenNotMatchException extends CommaException {
    private final static String MESSAGE = "Refresh token이 일치하지 않습니다.";

    public RefreshTokenNotMatchException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 400;
    }
}
