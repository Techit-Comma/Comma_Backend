package com.bitharmony.comma.global.exception.donation;

import com.bitharmony.comma.global.exception.CommaException;

public class JobKeyDuplicationException extends CommaException {

    private static String MESSAGE = "이미 존재하는 작업입니다.";

    public JobKeyDuplicationException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 500;
    }
}
