package com.bitharmony.comma.global.exception.community;


import com.bitharmony.comma.global.exception.CommaException;

public class DeleteFileFailureException extends CommaException {

    private final static String MESSAGE = "파일 삭제에 실패하였습니다.";

    public DeleteFileFailureException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 400;
    }
}
