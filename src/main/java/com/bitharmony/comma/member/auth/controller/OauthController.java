package com.bitharmony.comma.member.auth.controller;

import com.bitharmony.comma.global.provider.CookieProvider;
import com.bitharmony.comma.member.member.dto.MemberLoginResponse;
import com.bitharmony.comma.member.auth.service.OAuthService;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
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
    private final CookieProvider cookieProvider;
    private final static String REDIRECT_URL = "https://www.com-ma.shop?oauth=true";

    @GetMapping("/google")
    public String getGoogleLoginUrl() {
        return oAuthService.getGoogleRegisterUrl();
    }

    @GetMapping("/google/callback")
    public void loginToGoogleAccount(@RequestParam("code") String accessCode, HttpServletResponse response)
            throws IOException {
        MemberLoginResponse memberLoginResponse = oAuthService.googleLogin(accessCode);
        addTokenHeader(response, memberLoginResponse);
        response.sendRedirect(REDIRECT_URL);
    }

    @GetMapping("/github")
    public String getGithubLoginUrl() {
        return oAuthService.getGithubRegisterUrl();
    }

    @GetMapping("/github/callback")
    public void loginToGithubAccount(@RequestParam("code") String accessCode, HttpServletResponse response)
            throws IOException {
        MemberLoginResponse memberLoginResponse = oAuthService.githubLogin(accessCode);
        addTokenHeader(response, memberLoginResponse);
        response.sendRedirect(REDIRECT_URL);
    }

    private void addTokenHeader(HttpServletResponse response, MemberLoginResponse memberLoginResponse) {
        response.addHeader("Set-Cookie",
                cookieProvider.createAccessTokenCookie(memberLoginResponse.accessToken()).toString());
        response.addHeader("Set-Cookie",
                cookieProvider.createRefreshTokenCookie(memberLoginResponse.accessToken()).toString());
    }

}
