package com.bitharmony.comma.member.member.controller;

import com.bitharmony.comma.global.response.GlobalResponse;
import com.bitharmony.comma.member.member.dto.MemberLoginResponse;
import com.bitharmony.comma.member.member.service.OAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/oauth")
public class OauthController {

    private final OAuthService oAuthService;

    @GetMapping("/google")
    public String getGoogleLoginUrl() {
        return oAuthService.getGoogleRegisterUrl();
    }

    @GetMapping("/google/callback")
    public GlobalResponse<MemberLoginResponse> loginToGoogleAccount(@RequestParam("code") String accessCode) {
        return GlobalResponse.of("200", oAuthService.googleLogin(accessCode));
    }

    @GetMapping("/github")
    public String getGithubLoginUrl() {
        return oAuthService.getGithubRegisterUrl();
    }

    @GetMapping("/github/callback")
    public GlobalResponse<MemberLoginResponse> loginToGithubAccount(@RequestParam("code") String accessCode) {
        return GlobalResponse.of("200", oAuthService.githubLogin(accessCode));
    }

}
