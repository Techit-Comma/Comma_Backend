package com.bitharmony.comma.global.exception.playlist;

import com.bitharmony.comma.global.exception.CommaException;

public class PlaylistAlbumNotFoundException extends CommaException {
    private final static String MESSAGE = "플레이리스트 내 해당 앨범 정보가 없습니다.";

    public PlaylistAlbumNotFoundException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 502;
    }
}