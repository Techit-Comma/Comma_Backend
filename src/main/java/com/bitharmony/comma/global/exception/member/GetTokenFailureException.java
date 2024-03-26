package com.bitharmony.comma.global.exception.member;

import com.bitharmony.comma.global.exception.CommaException;

public class GetTokenFailureException extends CommaException {
    private final static String MESSAGE = "토큰 요청에 실패했습니다.";

    public GetTokenFailureException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 500;
    }
}
