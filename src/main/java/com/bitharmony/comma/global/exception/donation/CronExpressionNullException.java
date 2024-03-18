package com.bitharmony.comma.global.exception.donation;

import com.bitharmony.comma.global.exception.CommaException;

public class CronExpressionNullException extends CommaException {

    private final static String MESSAGE = "Cron 표현식을 찾을 수 없습니다.";

    public CronExpressionNullException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 501;
    }
}
