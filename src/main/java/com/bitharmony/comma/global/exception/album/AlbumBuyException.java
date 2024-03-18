package com.bitharmony.comma.global.exception.album;

import com.bitharmony.comma.global.exception.CommaException;

public class AlbumBuyException extends CommaException {
	private final static String MESSAGE = "앨범을 구매하는데 실패했습니다. 다시 시도해주세요.";

	public AlbumBuyException() { // 생성자
		super(MESSAGE);
	}

	@Override
	public int getStatusCode() {
		return 400;
	}
}
