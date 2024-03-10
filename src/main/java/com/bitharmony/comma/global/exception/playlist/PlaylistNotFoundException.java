package com.bitharmony.comma.global.exception.playlist;

import com.bitharmony.comma.global.exception.CommaException;

public class PlaylistNotFoundException extends CommaException {
    private final static String MESSAGE = "찾으시는 플레이리스트가 없습니다.";

    public PlaylistNotFoundException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 502;
    }
}