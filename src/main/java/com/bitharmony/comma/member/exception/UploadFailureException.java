package com.bitharmony.comma.member.exception;

import com.bitharmony.comma.global.exception.CommaException;

public class UploadFailureException extends CommaException {
    private final static String MESSAGE = "프로필 이미지 업로드에 실패하였습니다.";

    public UploadFailureException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 400;
    }
}
