package com.bitharmony.comma.global.exception.credit;

import com.bitharmony.comma.global.exception.CommaException;

public class NoCreditLogsException extends CommaException {

    private final static String MESSAGE = "크레딧 내역이 없습니다."; // 필요한 메시지 삽입.

    public NoCreditLogsException() { // 생성자
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() { // 메서드 구현
        return 400; // 상황에 맞게 상태 코드 변경
    }
}