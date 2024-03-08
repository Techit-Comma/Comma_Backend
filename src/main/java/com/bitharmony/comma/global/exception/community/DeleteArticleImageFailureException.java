package com.bitharmony.comma.global.exception.community;


import com.bitharmony.comma.global.exception.CommaException;

public class DeleteArticleImageFailureException extends CommaException {

    private final static String MESSAGE = "게시글 이미지 삭제에 실패하였습니다.";

    public DeleteArticleImageFailureException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 400;
    }
}
