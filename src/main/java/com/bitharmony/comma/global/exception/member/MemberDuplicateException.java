package com.bitharmony.comma.global.exception.member;

import com.bitharmony.comma.global.exception.CommaException;

public class MemberDuplicateException extends CommaException {
    private final static String MESSAGE = "이미 존재하는 유저가 있습니다.";

    public MemberDuplicateException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 404;
    }
}
