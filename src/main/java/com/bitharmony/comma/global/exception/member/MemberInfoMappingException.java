package com.bitharmony.comma.global.exception.member;

import com.bitharmony.comma.global.exception.CommaException;

public class MemberInfoMappingException extends CommaException {
    private final static String MESSAGE = "회원 정보 설정에 문제가 발생했습니다.";

    public MemberInfoMappingException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 500;
    }
}
