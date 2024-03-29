package com.bitharmony.comma.global.exception.member;

import com.bitharmony.comma.global.exception.CommaException;

public class DeleteOldProfileFailureException extends CommaException {
    private final static String MESSAGE = "기존 프로필 이미지 삭제에 실패하였습니다.";

    public DeleteOldProfileFailureException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 400;
    }
}
