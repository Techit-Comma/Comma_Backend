package com.bitharmony.comma.global.exception.donation;

import com.bitharmony.comma.global.exception.CommaException;

public class DonationRegularNotFoundException extends CommaException {

    private final static String MESSAGE = "주어진 정보로 등록된 정기후원을 찾을 수 없습니다.";

    public DonationRegularNotFoundException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 501;
    }
}
