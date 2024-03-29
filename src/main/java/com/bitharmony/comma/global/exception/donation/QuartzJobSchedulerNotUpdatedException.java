package com.bitharmony.comma.global.exception.donation;

import com.bitharmony.comma.global.exception.CommaException;

public class QuartzJobSchedulerNotUpdatedException extends CommaException {

    private final static String MESSAGE = "업데이트된 작업을 스케줄러에 등록하는데 실패했습니다.";

    public QuartzJobSchedulerNotUpdatedException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 500;
    }
}
