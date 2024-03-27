package com.bitharmony.comma.global.provider;

import java.time.Duration;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieProvider {

    public ResponseCookie createAccessTokenCookie(String accessToken) {
        return ResponseCookie.from("accessToken", accessToken)
                .domain("www.com-ma.shop")
                .path("/")
                .httpOnly(false)
                .secure(false)
                .maxAge(Duration.ofHours(1))
                .build();
    }

    public ResponseCookie createRefreshTokenCookie(String refreshToken) {
        return ResponseCookie.from("refreshToken", refreshToken)
                .domain("www.com-ma.shop")
                .path("/")
                .httpOnly(false)
                .secure(false)
                .maxAge(Duration.ofDays(7))
                .build();
    }
}
