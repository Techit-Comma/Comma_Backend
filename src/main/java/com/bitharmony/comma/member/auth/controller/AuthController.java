package com.bitharmony.comma.member.auth.controller;

import com.bitharmony.comma.global.response.GlobalResponse;
import com.bitharmony.comma.member.auth.dto.JwtRegenerateRequest;
import com.bitharmony.comma.member.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final AuthService authService;

    @PostMapping("/reissue")
    public GlobalResponse reissue(@RequestBody JwtRegenerateRequest jwtRegenerateRequest) {
        log.info("refreshToken = {}", jwtRegenerateRequest.refreshToken());
        return GlobalResponse.of("200", authService.reissue(jwtRegenerateRequest));
    }
}